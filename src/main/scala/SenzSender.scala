import java.net.{DatagramPacket, InetAddress, DatagramSocket}

import akka.actor.{ReceiveTimeout, Actor}
import scala.concurrent.duration._

case class Send(socket: DatagramSocket, msg: String)

/**
 * Created by eranga on 1/10/16.
 */
class SenzSender extends Actor {
  context.setReceiveTimeout(5 seconds)

  override def receive: Receive = {
    case Send(socket, msg) =>
      // TODO validate sign, encrypt the senz

      sendSenz(msg, socket)
    case ReceiveTimeout =>
      //resend message
      //sendSenz()
  }

  def sendSenz(msg: String, socket: DatagramSocket) = {
    val host = InetAddress.getByName("10.4.1.29")
    val port = 9999

    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, host, port)
    socket.send(senzOut)
  }
}
