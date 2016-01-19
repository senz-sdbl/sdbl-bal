package db

import com.datastax.driver.core.{Cluster, Session}
import config.Configuration

import scala.concurrent.Future

/**
 * Created by eranga on 1/15/16.
 */
object SenzdbConnector extends Configuration {

//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  lazy val session: Future[Session] = {
//    val clusterBuilder = Cluster.builder()
//    clusterBuilder.addContactPoint(cassandradbHost)
//    clusterBuilder.withPort(9042)
//
//    val cluster = clusterBuilder.build()
//    Future {
//      cluster.connect(cassandradbKeyspace)
//    }
//  }
}
