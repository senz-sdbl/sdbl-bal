package actors.handlers

import actors.SendSenz
import akka.actor.Actor

import scala.concurrent.duration._

case class Message(senz: String)

case class RegistrationDone()

case class RegistrationFail()


/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler(regSenz: String) extends Actor {

  import context._

  val senzSender = context.actorSelection("/user/SenzSender")
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Message(regSenz))

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  override def receive: Receive = {
    case Message(senz) =>
      senzSender ! SendSenz(senz)
    case RegistrationDone =>
      // success
      println("Registration done")
      cancellable.cancel()
      context.stop(self)
    case RegistrationFail =>
      // fail
      println("Registration fail")
      cancellable.cancel()
      context.stop(self)
  }
}
