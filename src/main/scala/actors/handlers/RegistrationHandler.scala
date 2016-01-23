package actors.handlers

import java.net.DatagramSocket

import akka.actor.Actor
import utils.SenzUtils

import scala.concurrent.duration._

case class RegistrationMessage(senz: String, counter: Int)

/**
 * Created by eranga on 1/22/16.
 */
class RegistrationHandler(socket: DatagramSocket) extends Actor {

  import context._

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  // send registration senz
  val registrationSenz = SenzUtils.getRegistrationSenz()

  override def receive: Receive = {
    case Message(senz, counter) =>
      if (counter < 3) {
        //senzSender ! RegisterSenz(senz)
        context.system.scheduler.scheduleOnce(8 seconds, self, Message(senz, counter + 1))
      } else {
        // stop actor
        context.stop(self)
      }
    case RegistrationDone =>
      // success
      context.stop(self)
    case AlreadyRegistered =>
      // already registered user
      context.stop(self)
    case RegistrationFail =>
      // fail
      println("Registration fail")
  }

}
