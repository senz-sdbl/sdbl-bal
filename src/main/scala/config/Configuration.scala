package config

import com.typesafe.config.ConfigFactory

import util.Try

/**
 * Load configurations define in application.conf from here
 *
 * @author eranga herath(erangaeb@gmail.com)
 */
trait Configuration {
  // config object
  val config = ConfigFactory.load()

  // senz config
  lazy val switchName = Try(config.getString("senz.switch-name")).getOrElse("")
  lazy val clientName = Try(config.getString("senz.client-name")).getOrElse("")

  // server config
  lazy val switchHost = Try(config.getString("switch.host")).getOrElse("localhost")
  lazy val switchPort = Try(config.getInt("switch.port")).getOrElse(9999)

  // db config
  lazy val dbHost = Try(config.getString("db.host")).getOrElse("dev.localhost")
  lazy val dbPort = Try(config.getInt("db.port")).getOrElse(27017)
  lazy val dbName = Try(config.getString("db.name")).getOrElse("senz")

  // keys config
  lazy val keysDir = Try(config.getString("keys.dir")).getOrElse(".keys")
  lazy val publicKeyLocation = Try(config.getString("keys.public-key-location")).getOrElse(".keys/id_rsa.pub")
  lazy val privateKeyLocation = Try(config.getString("keys.private-key-location")).getOrElse(".keys/id_rsa")
}
