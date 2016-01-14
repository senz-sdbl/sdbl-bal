package actors

import akka.actor.Actor
import utils.SenzUtils
import scala.concurrent.duration._

case class Ping()

/**
 * Created by eranga on 1/12/16.
 */
class PingSender extends Actor {

  import context._

  override def preStart = {
    println("----path----- " + context.self.path)
  }

  //val senzSender = context.actorSelection("../SenzSender")
  val senzSender = context.actorSelection("/user/SenzSender")

  override def receive: Receive = {
    case Ping =>
      // send ping via senz sender
      val ping = SenzUtils.getPingSenz()
      senzSender ! Send(ping)

      // re schedule to run on one minute
      context.system.scheduler.scheduleOnce(1 minutes, self, Ping)
  }
}
