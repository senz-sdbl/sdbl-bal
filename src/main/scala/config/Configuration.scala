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

  // server config
  lazy val serviceHost = Try(config.getString("service.host")).getOrElse("localhost")
  lazy val servicePort = Try(config.getInt("service.port")).getOrElse(9999)

}
