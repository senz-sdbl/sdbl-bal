package actors.handlers

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Cancellable, Actor}
import config.Configuration
import utils.{Senz, SenzParser}


import scala.concurrent.duration._
import scala.concurrent.{Await, Future, TimeoutException}

case class RegSenz(senz: String, counter: Int)

case class RegDone()

case class RegFail()

case class Registered()

/**
 * Created by eranga on 1/22/16.
 */
class RegistrationHandler(socket: DatagramSocket) extends Actor with Configuration {

  import context._

  private var scheduler: Cancellable = _

  override def preStart(): Unit = {
    println("----started----- " + context.self.path)
    import scala.concurrent.duration._
    scheduler = context.system.scheduler.schedule(
      initialDelay = 0 seconds,
      interval = 5 seconds,
      receiver = self,
      message = RegSenz
    )
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  override def receive: Receive = {
    case RegSenz(senz, counter) =>
      println("===================")
      println(sender.getClass.getName)
      println(sender.path)
      println(context.parent.getClass)
      println("===================")
      if (counter < 3) {
        sendSenz(senz)
        val future = readSenz(RegSenz(senz, counter))
        handleSenz(RegSenz(senz, counter), future)
      } else {
        // stop actor
        println(sender.getClass.getName)
        println(sender.path)
        context.parent ! RegFail
        context.stop(self)
      }
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }

  def readSenz(senz: RegSenz): Future[Senz] = {
    // read response via future
    val future = Future {
      val bufSize = 1024
      val buf = new Array[Byte](bufSize)
      val senzIn = new DatagramPacket(buf, bufSize)
      socket.receive(senzIn)

      val msg = new String(senzIn.getData)
      SenzParser.getSenz(msg)
    }

    future
  }

  def handleSenz(senz: RegSenz, future: Future[Senz]) = {
    // handler read timeout
    try {
      val senzIn = Await.result(future, 3 second)
      senzIn.attributes.get("#msg") match {
        case Some("REGISTRATION_DONE") =>
          println("done")
          context.stop(self)
        case Some("REGISTRATION_FAIL") =>
          println("fail")
          context.stop(self)
        case Some("ALREADY_REGISTERED") =>
          println("already registered...")
          context.stop(self)
        case _ =>
          self ! RegSenz(senz.senz, senz.counter + 1)
      }
    } catch {
      case e: TimeoutException =>
        println(e.toString)
        self ! RegSenz(senz.senz, senz.counter + 1)
    }
  }

}