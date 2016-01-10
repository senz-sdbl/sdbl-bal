import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, ReceiveTimeout}

import scala.concurrent.duration._

case class Init()

case class Send(socket: DatagramSocket, msg: String)

/**
 * Created by eranga on 1/10/16.
 */
class SenzSender extends Actor {
  override def receive: Receive = {
    case Init =>
      // init function
      println("init sender")
    case Send(socket, msg) =>
      context.setReceiveTimeout(5 seconds)
      println("send message")
      // TODO validate sign, encrypt the senz

      sendSenz(msg, socket)
    case ReceiveTimeout =>
      //resend message
      //sendSenz()
      println("timeout")
  }

  def sendSenz(msg: String, socket: DatagramSocket) = {
    val host = InetAddress.getByName("10.4.1.29")
    val port = 9999

    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, host, port)
    socket.send(senzOut)
  }
}
