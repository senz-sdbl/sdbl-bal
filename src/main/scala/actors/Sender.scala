package actors

import java.net.{DatagramSocket, InetAddress, DatagramPacket}

import config.Configuration

/**
 * Created by eranga on 1/23/16.
 */
trait Sender extends Configuration {
  def sendSenz(socket: DatagramSocket, msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
