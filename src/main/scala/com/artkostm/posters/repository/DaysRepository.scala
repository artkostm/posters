package com.artkostm.posters.repository

import com.artkostm.posters.model._
import com.artkostm.posters.repository.util.DBSetupOps
import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.jdbc.{GetResult, PositionedParameters, SQLActionBuilder, SetParameter}
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait EventsDayTable { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  private[EventsDayTable] class Days(tag: Tag) extends Table[EventsDay](tag, "days") {
    def date: Rep[DateTime] = column[DateTime]("date")
    def categories: Rep[List[Category]] = column[List[Category]]("categories")

    def * : ProvenShape[EventsDay] = (date, categories) <> (EventsDay.tupled, EventsDay.unapply)
    def pk = primaryKey("pk_days", date)
  }

  protected val Days = TableQuery[Days]
}

trait DaysRepository extends EventsDayTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>

  import profile.api._

  def setUpDays()(implicit ctx: ExecutionContext) =
    setUp(Days, Days.filter(day => day.date < DateTime.now.minusDays(1)).delete)

  def saveDay(day: EventsDay): Future[Int] = db.run(Days.insertOrUpdate(day.copy(date = day.date.withTimeAtStartOfDay())))

  def findDay(date: DateTime): Future[Option[EventsDay]] =
    db.run(Days.filter(d => d.date === date.withTimeAtStartOfDay()).result.headOption)

  import Category._

  implicit val GetDateTime = GetResult[DateTime](r => new DateTime(r.nextTimestamp()))
  implicit val GetCategory = GetResult[Category](r => Json.parse(r.nextString()).as[Category])
  implicit val SetDateTime = SetParameter[DateTime]((dt: DateTime, pp: PositionedParameters) => pp.setTimestamp(new java.sql.Timestamp(dt.toDate.getTime)))

  def findCategory(date: DateTime, name: String): Future[Option[Category]] = db.run(
    sql"""
         SELECT obj FROM days d, jsonb_array_elements(d.categories) obj WHERE date = ${date.withTimeAtStartOfDay()} AND obj->>'name' = $name
       """.as[Category].headOption)

  private def concat(a: SQLActionBuilder, b: SQLActionBuilder): SQLActionBuilder = {
    SQLActionBuilder(a.queryParts ++ b.queryParts, (p: Unit, pp: PositionedParameters) => {
      a.unitPConv.apply(p, pp)
      b.unitPConv.apply(p, pp)
    })
  }

  private def values[T](xs: TraversableOnce[T])(implicit sp: SetParameter[T]): SQLActionBuilder = {
    var b = sql"("
    var first = true
    xs.foreach { x =>
      if (first) first = false
      else b = concat(b, sql",")
      b = concat(b, sql"$x")
    }
    concat(b, sql")")
  }

  def findCategories(date: DateTime, names: List[String]): Future[Vector[Category]] = db.run(
    concat(
      sql"""
         SELECT obj FROM days d, jsonb_array_elements(d.categories) obj WHERE date = ${date.withTimeAtStartOfDay()} AND obj->>'name' IN
       """, values(names)).as[Category])

  def findCategories(startDate: DateTime, endDate: DateTime, names: List[String]): Future[Vector[Category]] = db.run(
    concat(sql"""
         SELECT obj FROM days d, jsonb_array_elements(d.categories) obj WHERE date >= $startDate AND date <= $endDate
       """, concat(sql""" AND obj->>'name' IN """, values(names))).as[Category])
}

trait TestRepo extends TestTable {
  this: JsonSupportDbComponent =>

  import driver.api._

  def test(names: List[String]) = {

    val q = for {
      data <- testQuery.map(_.data.arrayElements)
      if (data.+>>("name")) inSet names
    } yield data

    q.result.statements.foreach(println)
    null
  }

}