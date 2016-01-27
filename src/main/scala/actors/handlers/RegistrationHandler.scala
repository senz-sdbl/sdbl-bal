package actors.handlers

import java.net.{DatagramPacket, DatagramSocket, InetAddress}
import java.util.concurrent.ScheduledFuture

import actors.{InitReader, Ping, Sender, SendSenz}
import akka.actor.{Cancellable, Scheduler, Actor}
import config.Configuration
import crypto.RSAUtils
import utils.{SenzUtils, Senz, SenzParser}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, TimeoutException}

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

  val cancellable = system.scheduler.schedule(0 milliseconds, 5 seconds, self, Reg(SenzUtils.getRegistrationSenz()))

  override def preStart = {
    println("----path----- " + context.self.path)

    //val registrationSenz = SenzUtils.getRegistrationSenz()
    //println(registrationSenz + "-----")
  }

  override def receive: Receive = {
    case InitReg =>

    case RegSenz(senz, counter) =>
      if (counter < 3) {
        senzSender ! SendSenz(senz)
      } else {
        println("timeouttt")
        context.stop(self)
      }

      context.system.scheduler.scheduleOnce(5 seconds, self, RegSenz(senz, counter + 1))
    case Reg(senz) =>
      senzSender ! SendSenz(senz)
    case RegDone =>
      println("doneeeee")
      cancellable.cancel()
      context.stop(self)
    case RegFail =>
      println("faillll")
      cancellable.cancel()
      context.stop(self)
    case Registered =>
      println("registered....")

      cancellable.cancel()
      pingSender ! Ping
      senzReader ! InitReader

      context.stop(self)
  }

  //  def sendSenz(msg: String) = {
  //    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
  //    socket.send(senzOut)
  //  }
  //
  //  def readSenz(senz: RegSenz): Future[Senz] = {
  //    // read response via future
  //    val future = Future {
  //      val bufSize = 1024
  //      val buf = new Array[Byte](bufSize)
  //      val senzIn = new DatagramPacket(buf, bufSize)
  //      socket.receive(senzIn)
  //
  //      val msg = new String(senzIn.getData)
  //      SenzParser.getSenz(msg)
  //    }
  //
  //    future
  //  }
  //
  //  def handleSenz(senz: RegSenz, future: Future[Senz]) = {
  //    // handler read timeout
  //    try {
  //      val senzIn = Await.result(future, 3 second)
  //      senzIn.attributes.get("#msg") match {
  //        case Some("REGISTRATION_DONE") =>
  //          println("done")
  //          context.stop(self)
  //        case Some("REGISTRATION_FAIL") =>
  //          println("fail")
  //          context.stop(self)
  //        case Some("ALREADY_REGISTERED") =>
  //          println("already registered...")
  //          context.stop(self)
  //        case _ =>
  //          self ! RegSenz(senz.senz, senz.counter + 1)
  //      }
  //    } catch {
  //      case e: TimeoutException =>
  //        println(e.toString)
  //        self ! RegSenz(senz.senz, senz.counter + 1)
  //    }
  //  }

}