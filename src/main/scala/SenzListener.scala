import java.net.{InetAddress, DatagramPacket, DatagramSocket}

import akka.actor.Actor

case class InitListener(socket: DatagramSocket)

/**
 * Created by eranga on 1/9/16.
 */
class SenzListener extends Actor {
  override def receive: Receive = {
    case InitListener(socket) => {
      val bufSize = 1024
      val buf = new Array[Byte](bufSize)
      val host = InetAddress.getByName("10.4.1.29")
      val port = 9999

      val msg = "init"

      // send packet first
      val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, host, port)
      socket.send(senzOut)

      // receiving packet
      val senzIn = new DatagramPacket(buf, bufSize)

      // listen for udp socket in order to receive messages
      while (true) {
        socket.receive(senzIn)
        val text = new String(senzIn.getData)
        println("received--: " + text)

        // TODO handle received senz via a actor
      }
    }
  }
}