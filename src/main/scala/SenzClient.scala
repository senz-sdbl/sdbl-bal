import java.net.DatagramSocket

import akka.actor.{ActorSystem, Props}

/**
 * Created by eranga on 1/9/16.
 */
object SenzClient extends App {

  implicit val system = ActorSystem("senz")

  val sock = new DatagramSocket()

  // initialize actors
  val senzListener = system.actorOf(Props[SenzListener], name = "SenzListener")
  val senzSender = system.actorOf(Props[SenzSender], name = "SenzSender")
  val senzReader = system.actorOf(Props[SenzReader], name = "SenzReader")

  // Send initial messages to actors
  senzListener ! InitListener(sock)
  senzSender ! Init
  senzReader ! InitReader(sock)

}
