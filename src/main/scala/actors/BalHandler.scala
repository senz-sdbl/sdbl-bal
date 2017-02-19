package actors

import java.net.{InetAddress, InetSocketAddress}

import actors.SenzActor.SenzMsg
import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import components.BalDbComp
import config.Configuration
import org.slf4j.LoggerFactory
import protocols.{Bal, BalResp}
import utils.BalUtils

import scala.concurrent.duration._

case class ReqTimeout()

trait BalHandlerComp {

  this: BalDbComp =>

  object BalHandler {
    def props(bal: Bal): Props = Props(new balHandler(bal))
  }

  class balHandler(bal: Bal) extends Actor with Configuration {

    import context._

    def logger = LoggerFactory.getLogger(this.getClass)

    // we need senz sender to send reply back
    val senzActor = context.actorSelection("/user/SenzActor")

    // handle timeout in 5 seconds
    val timeoutCancellable = system.scheduler.scheduleOnce(5 seconds, self, ReqTimeout)

    // connect to epic tcp end
    val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
    IO(Tcp) ! Connect(remoteAddress)

    override def preStart() = {
      logger.debug("Start actor: " + context.self.path)
    }

    override def receive: Receive = {
      case c@Connected(remote, local) =>
        logger.debug("TCP connected")

        // balMsg from bal
        val balMsg = BalUtils.getBalMsg(bal)
        val msgStream = new String(balMsg.msgStream)

        logger.debug("Send BalMsg " + msgStream)

        // send balMsg
        val connection = sender()
        connection ! Register(self)
        connection ! Write(ByteString(balMsg.msgStream))

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
          case "close" =>
            logger.debug("Close")
            connection ! Close
          case _: ConnectionClosed =>
            logger.debug("ConnectionClosed")
            context.stop(self)
          case ReqTimeout =>
            // timeout
            logger.error("balTimeout")
            logger.debug("Resend balMsg " + msgStream)

            // resend bal
            connection ! Write(ByteString(balMsg.msgStream))
        }
      case CommandFailed(_: Connect) =>
        // failed to connect
        logger.error("CommandFailed[Failed to connect]")
    }

    def handleResponse(response: String, connection: ActorRef) = {
      // parse response and get 'balResp'
      BalUtils.getBalResp(response) match {
        case BalResp(_, "00", _) =>
          logger.debug("balaction done")
        case BalResp(_, status, _) =>
          logger.error("balaction fail with stats: " + status)
        case resp =>
          logger.error("Invalid response " + resp)
      }

      // TODO send response back
      val senz = s"DATA #amnt 2300 #acc 32323 @${bal.agent} ^sdblbal"
      senzActor ! SenzMsg(senz)

      // disconnect from tcp
      connection ! Close
    }
  }

}
