package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.Actor
import config.Configuration
import utils.SenzUtils

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
      println("send message " + msg)

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(serviceHost), servicePort)
    socket.send(senzOut)
  }
}
