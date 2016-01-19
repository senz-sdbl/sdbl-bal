package db

import eu.inn.binders._
import eu.inn.binders.cassandra.{SessionQueryCache, _}
import eu.inn.binders.naming.PlainConverter

/**
 * Created by eranga on 1/19/16.
 */
class Db(session: com.datastax.driver.core.Session) {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val cache = new SessionQueryCache[PlainConverter](session)

  case class User(id: Int, name: String)

  def insertUser(user: User) = cql"insert into user(id, name) values (?, ?)".bind(user).execute()

  // returns Future[Iterator[User]]
  def selectAllUsers = cql"select * from user".all[User]
}
