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
      val senz = Senz(SenzType.GET, sender, receiver, attr, signature)
      handlerGet(senz)
    case Senz(SenzType.PUT, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.PUT, sender, receiver, attr, signature)
      handlerPut(senz)
    case Senz(SenzType.SHARE, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.SHARE, sender, receiver, attr, signature)
      handlerShare(senz)
    case Senz(SenzType.DATA, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.DATA, sender, receiver, attr, signature)
      handleData(senz)
    case Senz(SenzType.PING, _, _, _, _) =>
    // we ignore ping messages
  }

  def handlerGet(senz: Senz) = {
    // save in database

    // send balance query to epic
  }

  def handlerPut(senz: Senz) = {
    // save in database

    // send transaction request to epic
  }

  def handleData(senz: Senz) = {
    senz.attributes.get("#msg") match {
      case "ShareDone" =>
      case "ShareFail" =>
      case "UserCreated" =>
      case "UserCreationFailed" =>
    }
  }

  def handlerShare(senz: Senz) = {
    // nothing to do with share
  }
}
