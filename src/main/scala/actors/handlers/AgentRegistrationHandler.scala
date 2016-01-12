package actors.handlers

import actors.Send
import akka.actor.{Actor, ReceiveTimeout}

import scala.concurrent.duration._

case class Message(senz: String)

case class Done()

/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler extends Actor {

  import context._

  val senzSender = context.actorSelection("../../SenzSender")
  context.setReceiveTimeout(30 seconds)

  override def receive: Receive = {
    case Message(senz) =>
      senzSender ! Send(senz)

      context.system.scheduler.scheduleOnce(8 seconds, self, Message(senz))
    case Done =>
      // success
      context.stop(self)
    case ReceiveTimeout =>
      // fail
      context.stop(self)
  }
}
