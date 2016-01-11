package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, ReceiveTimeout}
import config.Configuration
import utils.SenzUtils

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
      // send registration senz
      val registrationSenz = SenzUtils.getRegistrationSenz()
      sendSenz(registrationSenz)

      // return success response
      sender ! InitSuccess
    case Send(msg) =>
      context.setReceiveTimeout(5 seconds)
      println("send message " + msg)

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
