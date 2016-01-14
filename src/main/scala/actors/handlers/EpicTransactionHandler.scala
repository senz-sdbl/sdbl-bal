package actors.handlers

import akka.actor.Actor

case class Transaction()

/**
 * Created by eranga on 1/13/16.
 */
class EpicTransactionHandler extends Actor{
  override def receive: Receive = {
    case Transaction =>
  }
}
