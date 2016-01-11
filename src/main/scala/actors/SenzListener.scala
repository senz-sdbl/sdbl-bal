package actors

import java.net.{DatagramPacket, DatagramSocket}

import akka.actor.{Actor, Props}
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
        println("received--: " + msg)

        // handle received senz via a actor
        // parse senz first
        val senzHandler = context.actorOf(Props(new SenzHandler))
        val senz = SenzParser.getSenz(msg)
        senzHandler ! senz
      }
    }
  }
}
