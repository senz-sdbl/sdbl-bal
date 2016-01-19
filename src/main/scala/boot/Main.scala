package boot

import java.net.DatagramSocket

import actors._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.datastax.driver.core.Cluster
import crypto.RSAUtils
import db.Db

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created by eranga on 1/9/16.
 */
object Main extends App {

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


  val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
  val session = cluster.connect("dev")

  val db = new Db(session)

  db.insertUser(db.User(9, "John"))

  //val users = Await.result(db.selectAllUsers, 10 seconds)

  //println(users.toList)

  session.close()
  cluster.close()
}
