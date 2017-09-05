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
  // setup keys
  // generate key pair if not already generated
  SenzFactory.setupLogging()
  SenzFactory.setupKeys()
  RSAUtils.initRSAKeys()

  implicit val system = ActorSystem("senz")

  // start senz actor
  val senzActor = system.actorOf(SenzActor.props, name = "SenzActor")
  senzActor ! InitSenz
}
