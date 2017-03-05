package actors

import java.net.{InetAddress, InetSocketAddress}

import actors.SenzActor.SenzMsg
import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.Configuration
import org.slf4j.LoggerFactory
import protocols.{BalInq, BalInqResp}
import utils.BalanceUtils

import scala.concurrent.duration._

case class ReqTimeout()

object BalHandler {

  case class TransTimeout(retry: Int)

  def props(accInq: BalInq): Props = Props(new BalHandler(accInq))
}

class BalHandler(accInq: BalInq) extends Actor with Configuration {

  import BalHandler._
  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  // we need senz sender to send reply back
  val senzActor = context.actorSelection("/user/SenzActor")

  // connect to epic tcp end
  val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
  IO(Tcp) ! Connect(remoteAddress)

  // handle timeout in 15 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(15 seconds, self, TransTimeout(0))

  override def preStart() = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case c@Connected(remote, local) =>
      logger.debug("TCP connected")

      // transMsg from trans
      val inqMsg = BalanceUtils.getBalInqMsg(accInq)
      val msgStream = new String(inqMsg.msgStream)

      logger.debug("Send TransMsg " + msgStream)

      // send TransMsg
      val connection = sender()
      connection ! Register(self)
      connection ! Write(ByteString(msgStream))

      // handler response
      context become {
        case CommandFailed(w: Write) =>
          logger.error("CommandFailed[Failed to write]")
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Received : " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.debug("ConnectionClosed")
          context.stop(self)
        case ReqTimeout =>
          // timeout
          logger.error("balTimeout")
          logger.debug("Resend balMsg " + msgStream)

        // TODO resend inq
        // connection ! Write(ByteString(balMsg.msgStream))
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")
  }

  def handleResponse(response: String, connection: ActorRef) = {
    // parse response and get 'acc response'
    BalanceUtils.getBalInqResp(response) match {
      case BalInqResp(_, "00", _, bal) =>
        logger.debug(s"acc inq done $bal")

        // TODO send response back
        val senz = s"DATA #acc $bal @${accInq.agent} ^$senzieName"
        senzActor ! SenzMsg(senz)
      case BalInqResp(_, status, _, _) =>
        logger.error("acc inq fail with stats: " + status)
      case resp =>
        logger.error("invalid response " + resp)
    }

    // disconnect from tcp
    connection ! Close
  }

}
