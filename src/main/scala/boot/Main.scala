package boot

import java.net.DatagramSocket

import actors._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import crypto.RSAUtils
import db.{Agent, SenzCassandraCluster, SenzDb}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created by eranga on 1/9/16.
 */
object Main extends App with SenzCassandraCluster {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem("senz")

  // this is the datagram socket that uses to connect to senz switch
  val datagramSocket = new DatagramSocket()

  // first generate key pair if not already generated
  RSAUtils.initRSAKeys()

  // initialize actors
  val senzSender = system.actorOf(Props(classOf[SenzSender], datagramSocket), name = "SenzSender")
  val senzListener = system.actorOf(Props(classOf[SenzListener], datagramSocket), name = "SenzListener")
  val senzReader = system.actorOf(Props[SenzReader], name = "SenzReader")
  val pingSender = system.actorOf(Props[PingSender], name = "PingSender")

  // init sender and wait until its success   
  implicit val timeout = Timeout(5 seconds)
  val future = senzSender ? InitSender
  future onComplete {
    case Success(result) =>
      // start listener, ping sender and reader
      senzListener ! InitListener
      //pingSender ! Ping
      senzReader ! InitReader
    case Failure(result) =>
      println("init fails")
  }

  //new SenzDb(cluster).insertUser(User(5, "eranga"))
}
