package handlers

import utils.{SenzType, Senz}

/**
 * Created by eranga on 1/14/16.
 */
object SenzHandler {

  def handle(senz: Senz) = {
    case Senz(SenzType.GET, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.GET, sender, receiver, attr, signature)
      handleGet(senz)
    case Senz(SenzType.PUT, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.PUT, sender, receiver, attr, signature)
      handlePut(senz)
    case Senz(SenzType.SHARE, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.SHARE, sender, receiver, attr, signature)
      handlerShare(senz)
    case Senz(SenzType.DATA, sender, receiver, attr, signature) =>
      val senz = Senz(SenzType.DATA, sender, receiver, attr, signature)
      handleData(senz)
    case Senz(SenzType.PING, _, _, _, _) =>
    // we ignore ping messages
  }

  def handleGet(senz: Senz) = {
    // save in database

    // send balance query to epic
  }

  def handlePut(senz: Senz) = {
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
