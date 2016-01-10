import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, ReceiveTimeout}

import scala.concurrent.duration._

case class Init()

case class Send(msg: String)

case class InitSuccess()

case class InitFail()

/**
 * Created by eranga on 1/10/16.
 */
class SenzSender(socket: DatagramSocket) extends Actor {
  override def receive: Receive = {
    case Init =>
      // TODO send registration packet first
      val msg = "init"
      sendSenz(msg)

      // return success response
      sender ! InitSuccess
    case Send(msg) =>
      context.setReceiveTimeout(5 seconds)
      println("send message")

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
    case ReceiveTimeout =>
      //resend message
      //sendSenz()
      println("timeout")
  }

  def sendSenz(msg: String) = {
    val host = InetAddress.getByName("10.4.1.29")
    val port = 9999

    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, host, port)
    socket.send(senzOut)
  }
}
