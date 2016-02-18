package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, Props}
import config.Configuration
import utils.SenzUtils

case class InitSender()

case class SendSenz(msg: String)


/**
 * Created by eranga on 1/10/16.
 */
class SenzSender(socket: DatagramSocket) extends Actor with Configuration {

  override def preStart = {
    println("----path----- " + context.self.path)
  }

  override def receive: Receive = {
    case InitSender =>
      val regSenz = SenzUtils.getRegistrationSenz()
      context.actorOf(Props(classOf[RegistrationHandler], regSenz), "RegistrationHandler")
    case SendSenz(msg) =>
      //println("send message " + msg)

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
