package actors.handlers

import actors.SendSenz
import akka.actor.Actor
import handlers.SignatureVerificationFailed

import scala.concurrent.duration._

case class Message(senz: String)

case class RegistrationDone()

case class RegistrationFail()

case class RegTimeout()


/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler(regSenz: String) extends Actor {

  import context._

  val senzSender = context.actorSelection("/user/SenzSender")
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Message(regSenz))

  // send timeout message after 12 seconds
  val timeoutCancellable = system.scheduler.scheduleOnce(10 seconds, self, RegTimeout)

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  override def receive: Receive = {
    case Message(senz) =>
      println("send regmsg " + senz)
      context.setReceiveTimeout(30 milliseconds)
      senzSender ! SendSenz(senz)
    case RegistrationDone =>
      // success
      println("Registration done")
      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case RegistrationFail =>
      // fail
      println("Registration fail")
      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case SignatureVerificationFailed =>
      println("singature virification failed")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
    case RegTimeout =>
      println("Timeouttttt")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
  }
}
