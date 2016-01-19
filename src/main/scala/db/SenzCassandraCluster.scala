package db

import com.datastax.driver.core.Cluster
import config.Configuration

trait CassandraCluster {
  def cluster: Cluster
}

/**
 * Created by eranga on 1/19/16.
 */
trait SenzCassandraCluster extends CassandraCluster with Configuration {
  lazy val cluster: Cluster = {
    Cluster.builder().
      addContactPoint("127.0.0.1").
      build()
  }
}
