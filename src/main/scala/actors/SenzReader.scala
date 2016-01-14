package actors

import actors.handlers.{Message, AgentRegistrationHandler}
import akka.actor.{Props, Actor}
import crypto.RSAUtils

case class InitReader()

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

  override def preStart = {
    println("----path----- " + context.self.path)
  }

  override def receive: Receive = {
    case InitReader => {
      // listen for user inputs form commandline
      while (true) {
        // read user input from the command line
        val inputSenz = scala.io.StdIn.readLine()

        // TODO validate senz

        if (!inputSenz.isEmpty) {
          // sign senz
          val signature = RSAUtils.signSenz(inputSenz.trim)
          val signedSenz = s"$inputSenz $signature"

          // start actor to handle the senz
          val handler = context.actorOf(Props(new AgentRegistrationHandler), "AgentRegistrationHandler")
          handler ! Message(signedSenz, 0)
        }
      }
    }
  }
}
