package actors.handlers

import akka.actor.Actor
import utils.{Senz, SenzType}

case class SenzMessage(senz: Senz)

/**
 * Created by eranga on 1/10/16
 */
class SenzHandler extends Actor {

  val senzSender = context.actorSelection("../../SenzSender")

  override def receive: Receive = {
    case Senz(SenzType.GET, sender, receiver, attr, signature) =>
      Senz(SenzType.GET, sender, receiver, attr, signature)
    case Senz(SenzType.DATA, sender, receiver, attr, signature) =>
      Senz(SenzType.DATA, sender, receiver, attr, signature)

      attr.get("#msg") match {
        case "ShareDone" =>
        case "ShareFail" =>
        case "UserCreated" =>
        case "UserCreationFailed" =>
      }
    case Senz(SenzType.PING, sender, receiver, attr, signature) =>
      Senz(SenzType.DATA, sender, receiver, attr, signature)
  }
}
