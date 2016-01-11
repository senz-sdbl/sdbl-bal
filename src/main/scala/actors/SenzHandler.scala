package actors

import akka.actor.Actor

/**
 * Created by eranga on 1/10/16.
 */
class SenzHandler extends Actor {

  val senzSender = context.actorSelection("../../SenzSender")

  override def receive: Receive = {
    case msg: String =>
      // handle message
      println("handle message")

      //senzSender ! Send("yahooo")
  }
}
