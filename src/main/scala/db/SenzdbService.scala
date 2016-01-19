package db

/**
 * Created by eranga on 1/15/16.
 */
object SenzdbService extends People {
  def peopleByFirstName(firstName: String): concurrent.Future[Seq[Person]] = {
    select.where(_.firstName eqs firstName).limit(5000).fetch()
  }
}
