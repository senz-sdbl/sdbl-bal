package actors

import akka.actor.Actor
import utils.{SenzType, Senz}

/**
 * Created by eranga on 1/10/16.
 */
class SenzHandler extends Actor {

  val senzSender = context.actorSelection("../../SenzSender")

  override def receive: Receive = {
    case senz: Senz =>
      senz match {
        case Senz(SenzType.GET, _, _, _, _) =>
          // handle message
          println("handle message GET")
        case Senz(SenzType.DATA, _, _, _, _) =>
          println("handle message DATA")
          //senzSender ! Send("yahooo")
        case Senz(SenzType.PING, _, _, _, _) =>
      }
    case _ =>
      // unsupported message
  }
}
