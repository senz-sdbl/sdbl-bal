package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.Actor
import config.Configuration

case class InitSender()

case class RegisterSenz(senz: String)

case class Send(msg: String)

case class InitSuccess()

case class InitFail()

/**
 * Created by eranga on 1/10/16.
 */
class SenzSender(socket: DatagramSocket) extends Actor with Configuration {

  override def preStart = {
    println("----path----- " + context.self.path)
  }

  override def receive: Receive = {
    case RegisterSenz(senz) =>
      sendSenz(senz)

      // return success response
      sender ! InitSuccess
    case Send(msg) =>
      println("send message " + msg)

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
