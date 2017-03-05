package boot

import actors.SenzActor.InitSenz
import actors._
import akka.actor.ActorSystem
import crypto.RSAUtils
import utils.SenzFactory

/**
  * Created by eranga on 1/9/16.
  */
object Main extends App {
  // setup logging
  SenzFactory.setupLogging()

  // setup keys
  SenzFactory.setupKeys()

  implicit val system = ActorSystem("senz")

  // first generate key pair if not already generated
  RSAUtils.initRSAKeys()

  // start senz actor
  val senzActor = system.actorOf(SenzActor.props, name = "SenzActor")
  senzActor ! InitSenz
}
