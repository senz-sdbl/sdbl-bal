package db

import com.websudos.phantom.dsl._

import scala.concurrent.Future

case class Person(id: UUID, name: String, firstName: String)

/*
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

  def store(person: Person): Future[ResultSet] = {
    insert.value(_.id, person.id)
      .value(_.name, person.name)
      .value(_.firstName, person.firstName)
      .consistencyLevel_=(ConsistencyLevel.ALL)
      .future()
  }

  //  def getById(id: UUID): Future[Option[Person]] = {
  //    select.where(_.id eqs id).one()
  //  }
}
*/
