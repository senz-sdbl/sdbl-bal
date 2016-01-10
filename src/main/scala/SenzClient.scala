import java.net.DatagramSocket

import akka.actor.{ActorSystem, Props}

/**
 * Created by eranga on 1/9/16.
 */
object SenzClient extends App {

  implicit val system = ActorSystem("senz")

  val sock = new DatagramSocket()

  // Initialize senz listener
  val senzListener = system.actorOf(Props[SenzListener], name = "SenzListener")
  senzListener ! InitListener(sock)

  // initialize senz reader
  val senzReader = system.actorOf(Props[SenzReader], name = "SenzReader")
  senzReader ! InitReader(sock)
}
