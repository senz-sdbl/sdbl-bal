package actors

import akka.actor.Actor
import crypto.RSAUtils

case class InitReader()

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

  override def receive: Receive = {
    case InitReader => {
      // find sender actor
      val senzSender = context.actorSelection("../SenzSender")

      // listen for user inputs form commandline
      while (true) {
        // read user input from the command line
        val inputSenz = scala.io.StdIn.readLine()

        // TODO validate senz

        if (!inputSenz.isEmpty) {
          // sign senz
          val signature = RSAUtils.signSenz(inputSenz.trim)
          val signedSenz = s"$inputSenz $signature"

          senzSender ! Send(signedSenz)
        }
      }
    }
  }
}
