package actors.handlers

import actors.{InitReader, Ping, SendSenz, Sender}
import akka.actor.Actor
import config.Configuration
import utils.SenzUtils

import scala.concurrent.duration._

case class InitReg()

case class RegSenz(senz: String, counter: Int)

case class Reg(senz: String)

case class RegDone()

case class RegFail()

case class Registered()

/**
 * Created by eranga on 1/22/16.
 */
class RegistrationHandler() extends Actor with Configuration with Sender {

  import context._

  val senzSender = context.actorSelection("/user/SenzSender")
  val pingSender = context.actorSelection("/user/PingSender")
  val senzReader = context.actorSelection("/user/SenzReader")

  // scheduler to run on 5 seconds
  val regSenz = SenzUtils.getRegistrationSenz()
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Reg(regSenz))

  override def preStart = {
    println("----path----- " + context.self.path)
  }

  override def receive: Receive = {
    case Reg(senz) =>
      senzSender ! SendSenz(senz)
    case RegDone =>
      println("reg done")
      cancellable.cancel()
      context.stop(self)
    case RegFail =>
      println("reg fail")
      cancellable.cancel()
      context.stop(self)
    case Registered =>
      println("already reg....")

      // cancel scheduler
      cancellable.cancel()

      // start ping sender and senz reader
      pingSender ! Ping
      senzReader ! InitReader

      // stop the actor
      context.stop(self)
  }

}