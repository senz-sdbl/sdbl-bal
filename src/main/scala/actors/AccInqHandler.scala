package actors

import java.net.{InetAddress, InetSocketAddress}

import actors.AccInqHandler.AccInq
import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import org.slf4j.LoggerFactory
import protocols.Msg
import utils.AccInquiryUtils

import scala.concurrent.duration._

object AccInqHandler {

  case class AccInqMsg(msgStream: Array[Byte])

  case class AccInqResp(esh: String, status: String, auth: String, accs: String)

  case class AccInq(agent: String, nic: String)

  case class InqTimeout()

  def props(accInq: AccInq): Props = Props(new AccInqHandler(accInq))

}

class AccInqHandler(accInq: AccInq) extends Actor with AppConf {

  import AccInqHandler._
  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  // we need senz sender to send reply back
  val senzActor = context.actorSelection("/user/SenzActor")

  // connect to epic tcp end
  val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
  IO(Tcp) ! Connect(remoteAddress)

  // handle timeout in 15 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(15 seconds, self, InqTimeout)

  override def preStart() = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case c@Connected(remote, local) =>
      logger.debug("TCP connected")

      // inqMsg from
      val inqMsg = AccInquiryUtils.getAccInqMsg(accInq)
      val msgStream = new String(inqMsg.msgStream)

      logger.debug("Send AccInq " + msgStream)

      // send AccInq
      val connection = sender()
      connection ! Register(self)
      connection ! Write(ByteString(msgStream))

      // handler response
      context become {
        case CommandFailed(w: Write) =>
          logger.error("CommandFailed[Failed to write]")
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Response received : " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.debug("ConnectionClosed")
          context.stop(self)
        case InqTimeout =>
          // timeout
          logger.error("acc inq timeout")

        // TODO resend acc inq
        // connection ! Write(ByteString(balMsg.msgStream))
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")
  }

  def handleResponse(response: String, connection: ActorRef) = {
    // parse response and get 'acc response'
    AccInquiryUtils.getAccInqResp(response) match {
      case AccInqResp(_, "00", _, data) =>
        logger.debug(s"acc inq done $data")

        // parse acc response and find accounts
        val accs = (for (acc <- data.split("~")) yield acc.split("#")(1)).mkString("|")
        logger.debug(s"accs: $data")

        // send response back
        val senz = s"DATA #acc $accs @${accInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case AccInqResp(_, "11", _, _) =>
        logger.error(s"No account found for id ${accInq.nic}")

        // send empty response back
        val senz = s"DATA #acc @${accInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case AccInqResp(_, status, _, _) =>
        logger.error("acc inq fail with stats: " + status)

        // TODO send error response back
        // send empty response back
        val senz = s"DATA #acc @${accInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case resp =>
        logger.error("invalid response " + resp)
    }

    // disconnect from tcp
    connection ! Close
  }

}
