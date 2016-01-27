package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import actors.handlers.RegistrationHandler
import akka.actor.{Actor, Props}
import config.Configuration

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
      context.actorOf(Props[RegistrationHandler], "RegistrationHandler")
      //handler ! RegSenz(registrationSenz, 0)
      //sendSenz(registrationSenz)
    case SendSenz(msg) =>
      println("send message " + msg)

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
