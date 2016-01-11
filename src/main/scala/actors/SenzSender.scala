package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, ReceiveTimeout}
import config.Configuration

import scala.concurrent.duration._

case class InitSender()

case class Send(msg: String)

case class InitSuccess()

case class InitFail()

/**
 * Created by eranga on 1/10/16.
 */
class SenzSender(socket: DatagramSocket) extends Actor with Configuration {
  override def receive: Receive = {
    case InitSender =>
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
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(serviceHost), servicePort)
    socket.send(senzOut)
  }
}
