package actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, UdpConnected}
import akka.util.ByteString

case class InitMsg()
case class InitDone()

case class SenzMessage(msg: String)

/**
 * Created by eranga on 1/22/16.
 */
class SenzUdp(remote: InetSocketAddress) extends Actor {

  import context.system

  def receive = {
    case InitMsg =>
      println("INIT" + sender.path)
      IO(UdpConnected) ! UdpConnected.Connect(self, remote)
    case UdpConnected.Connected =>
      println("connected " + context.parent.path)
      context.parent ! InitDone
      //sender ! UdpConnected.Connected
      //context.become(ready(sender()))
      //self ! SenzMessage("Yahoo")
    case UdpConnected.Received(data) =>
      println(data.toString())
    // process data, send it on, etc
    case SenzMessage(msg) =>
      println("senz msg")

      sender ! UdpConnected.Send(ByteString(msg))
    case UdpConnected.Disconnect =>
      println("disconnect")
      sender ! UdpConnected.Disconnect
    case UdpConnected.Disconnected =>
      println("disconnectedddd")
  }
}