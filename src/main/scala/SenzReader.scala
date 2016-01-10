import java.net.DatagramSocket

import akka.actor.Actor

case class InitReader(socket: DatagramSocket)

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

  override def receive: Receive = {
    case InitReader(socket) => {
      // find sender actor
      val senzSender = context.actorSelection("../SenzSender")

      // read user input from the command line
      while (true) {
        val input = scala.io.StdIn.readLine()
        println("Input senz: " + input)

        senzSender ! Send(socket, input)
      }
    }
  }
}
