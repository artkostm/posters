package com.artkostm.posters.repository

import com.artkostm.posters.model.{Category, EventsDay, EventsDayTable}
import org.joda.time.DateTime
import play.api.libs.json.Json
import slick.dbio.DBIOAction
import slick.jdbc.{GetResult, PositionedParameters, SQLActionBuilder, SetParameter}
import slick.jdbc.meta.MTable

import scala.concurrent.{ExecutionContext, Future}

trait DaysRepository extends EventsDayTable { this: JsonSupportDbComponent =>
  import driver.plainAPI._

  def setUpDays()(implicit ctx: ExecutionContext): Future[Unit] = db.run {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(daysTableQuery.baseTableRow.tableName)) daysTableQuery.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, daysTableQuery.filter(day => day.date < DateTime.now.minusDays(1)).delete)
  }

  def save(day: EventsDay): Future[Int] = db.run(daysTableQuery.insertOrUpdate(day.copy(date = day.date.withTimeAtStartOfDay())))

  def find(date: DateTime): Future[Option[EventsDay]] = db.run {
    daysTableQuery.filter(d => d.date === date.withTimeAtStartOfDay()).result.headOption
  }

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
      if(first) first = false
      else b = concat(b, sql",")
      b = concat(b, sql"$x")
    }
    concat(b, sql")")
  }

  def findCategories(date: DateTime, names: List[String]): Future[Vector[Category]] = db.run(
    concat(sql"""
         SELECT obj FROM days d, jsonb_array_elements(d.categories) obj WHERE date = ${date.withTimeAtStartOfDay()} AND obj->>'name' IN
       """, values(names)).as[Category])
}
