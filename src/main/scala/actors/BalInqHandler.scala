package actors

import java.net.{InetAddress, InetSocketAddress}

import actors.BalInqHandler.BalInq
import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import protocols.Msg
import utils.{BalInqUtils, SenzLogger}

import scala.concurrent.duration._

object BalInqHandler {

  case class BalInq(agent: String, account: String)

  case class BalInqMsg(msgStream: Array[Byte])

  case class BalInqResp(esh: String, status: String, authCode: String, rst: String)

  case class BalInqTimeout()

  def props(accInq: BalInq): Props = Props(new BalInqHandler(accInq))

}

class BalInqHandler(balInq: BalInq) extends Actor with AppConf with SenzLogger {

  import BalInqHandler._
  import context._

  // we need senz sender to send reply back
  val senzActor = context.actorSelection("/user/SenzActor")

  // connect to epic tcp end
  val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
  IO(Tcp) ! Connect(remoteAddress)

  // handle timeout in 30 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(30.seconds, self, BalInqTimeout())

  override def preStart(): Unit = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case Connected(_, _) =>
      logger.debug("TCP connected")

      // transMsg from trans
      val inqMsg = BalInqUtils.getBalInqMsg(balInq)
      val msgStream = new String(inqMsg.msgStream)

      logger.debug("Send TransMsg " + msgStream)

      // send TransMsg
      val connection = sender()
      connection ! Register(self)
      connection ! Write(ByteString(msgStream))

      // handler response
      context become {
        case CommandFailed(_: Write) =>
          logger.error("CommandFailed[Failed to write]")
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Received : " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.error("ConnectionClosed before complete the trans")

          // cancel timer
          timeoutCancellable.cancel()

          // send error status back
          val senz = s"DATA #status ERROR @${balInq.agent} ^$senzieName"
          senzActor ! Msg(senz)

          // stop from here
          context.stop(self)
        case BalInqTimeout() =>
          // timeout
          logger.error("bal inq timeout")

          // send error status back
          val senz = s"DATA #status ERROR @${balInq.agent} ^$senzieName"
          senzActor ! Msg(senz)

          // stop from here
          context.stop(self)
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")

      // cancel timer
      timeoutCancellable.cancel()

      // send error status back
      val senz = s"DATA #status ERROR @${balInq.agent} ^$senzieName"
      senzActor ! Msg(senz)

      // stop from here
      context.stop(self)
  }

  def handleResponse(response: String, connection: ActorRef): Unit = {
    // parse response and get 'acc response'
    BalInqUtils.getBalInqResp(response) match {
      case BalInqResp(_, "00", _, bal) =>
        logger.info(s"bal inq success with $bal")

        // send response back with actual balance
        val balance = bal.substring(8, 20)
        val senz = s"DATA #bal $balance @${balInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case BalInqResp(_, status, _, _) =>
        logger.error("bal inq fail with stats: " + status)

        // send error response back
        val senz = s"DATA #status ERROR @${balInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case resp =>
        logger.error("invalid response " + resp)

        // send error response back
        val senz = s"DATA #status ERROR @${balInq.agent} ^$senzieName"
        senzActor ! Msg(senz)
    }

    // stop from here
    context.stop(self)
  }

}
