import java.net.DatagramSocket
import akka.actor.{Actor, Props}

case class InitReader(socket: DatagramSocket)

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

  override def receive: Receive = {
    case InitReader(socket) => {
      // read user input from the command line
      while (true) {
        val input = scala.io.StdIn.readLine()
        println("Input senz: " + input)

        // send senz via actor
        val sender = context.actorOf(Props(new SenzSender))
        sender ! Send(socket, input)
      }
    }
  }
}
