package actors

import java.net.{DatagramPacket, DatagramSocket}

import _root_.handlers.SenzHandler
import akka.actor.Actor
import utils.SenzParser

case class InitListener()

/**
 * Created by eranga on 1/9/16.
 */
class SenzListener(socket: DatagramSocket) extends Actor {

  override def receive: Receive = {
    case InitListener => {
      val bufSize = 1024
      val buf = new Array[Byte](bufSize)

      // receiving packet
      val senzIn = new DatagramPacket(buf, bufSize)

      // listen for udp socket in order to receive messages
      while (true) {
        socket.receive(senzIn)
        val msg = new String(senzIn.getData)

        // handle received senz
        // parse senz first
        val senz = SenzParser.getSenz(msg)
        SenzHandler.handle(senz)
      }
    }
  }
}
