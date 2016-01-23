package actors

import akka.actor.Actor
import utils.SenzUtils
import scala.concurrent.duration._

case class InitPing()

case class Ping()

/**
 * Created by eranga on 1/12/16.
 */
class PingSender extends Actor {

  import context._

  override def preStart = {
    println("----started----- " + context.self.path)
  }

  val senzUdp = context.actorSelection("/user/SenzUdp")

  override def receive: Receive = {
    case InitPing =>
      // initialize periodic ping messages
      self ! Ping

    case Ping =>
      // send ping via senz udp
      val ping = SenzUtils.getPingSenz()
      senzUdp ! SenzMessage(ping)

      // re schedule to run on one minute
      context.system.scheduler.scheduleOnce(10 seconds, self, Ping)
  }
}