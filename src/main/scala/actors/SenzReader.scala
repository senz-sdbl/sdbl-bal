package actors

import akka.actor.{Actor, Props}
import crypto.RSAUtils

case class InitReader()

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  override def receive: Receive = {
    case InitReader => {
      // listen for user inputs form commandline
      while (true) {
        println()
        println()
        println("--------------------------------------------------")
        println("ENTER #SENZ[SHARE #nic #nam #acc @agent_ ^sdblbal]")
        println("--------------------------------------------------")
        println()

        // read user input from the command line
        val inputSenz = scala.io.StdIn.readLine()

        // TODO validate senz

        if (!inputSenz.isEmpty) {
          // sign senz
          val senzSignature = RSAUtils.signSenz(inputSenz.trim.replaceAll(" ", ""))
          val signedSenz = s"$inputSenz $senzSignature"

          //println(signedSenz)

          // start actor to handle the senz
          context.actorOf(Props(classOf[AgentRegistrationHandler], signedSenz))
        }
      }
    }
  }
}