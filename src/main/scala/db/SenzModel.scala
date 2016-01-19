package db

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.dsl._

import scala.concurrent.Future

case class Person(id: UUID, name: String, firstName: String)

class People extends CassandraTable[People, Person] {
  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object name extends StringColumn(this)
  object firstName extends StringColumn(this)

  def fromRow(row: Row): Person = {
    Person(
      id(row),
      name(row),
      firstName(row)
    )
  }

  def insert() = {
    def insert(row: Person, session: Session): Future[Unit] = {
      insert.value(_.fooId, row.id)
        .value(_.bar, row.bar)
        .value(_.baz, row.baz)
        .future()(session)
        .map(_ => ())
    }
  }
}
