package actors.handlers

import actors.Send
import akka.actor.Actor

import scala.concurrent.duration._

case class Message(senz: String, counter: Int)

case class RegDone()

case class RegFail()

/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler extends Actor {

  override def preStart = {println("my path is----------: " + context.self.path)}

  import context._

  val senzSender = context.actorSelection("../../SenzSender")

  override def receive: Receive = {
    case Message(senz, counter) =>
      if (counter < 3) {
        senzSender ! Send(senz)
        context.system.scheduler.scheduleOnce(8 seconds, self, Message(senz, counter + 1))
      } else {
        // stop actor
        context.stop(self)
      }
    case RegDone =>
      // success
      context.stop(self)
    case RegFail =>
      // fail
      println("hooooooo")
  }
}
