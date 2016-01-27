package actors.handlers

import actors.SendSenz
import akka.actor.Actor

import scala.concurrent.duration._

case class Message(senz: String, counter: Int)

case class RegistrationDone()

case class RegistrationFail()

case class AlreadyRegistered()

case class ShareDone()

case class ShareFail()

/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler extends Actor {

  import context._

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  val senzSender = context.actorSelection("/user/SenzSender")

  override def receive: Receive = {
    case Message(senz, counter) =>
      if (counter < 3) {
        senzSender ! SendSenz(senz)
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
