package com.artkostm.posters.repository

import com.artkostm.posters.model._
import com.artkostm.posters.repository.util.DBSetupOps
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait EventsDayTable { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  private[EventsDayTable] class Days(tag: Tag) extends Table[EventsDay](tag, "days") {
    def date: Rep[DateTime]      = column[DateTime]("date")
    def categories: Rep[JsValue] = column[JsValue]("categories")

    def * : ProvenShape[EventsDay] = (date, categories) <> (EventsDay.tupled, EventsDay.unapply)
    def pk                         = primaryKey("pk_days", date)
  }

  protected val Days = TableQuery[Days]
}

trait DaysRepository extends EventsDayTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>

  import profile.api._

  val compiledCategoryByDate = Compiled { date: Rep[DateTime] =>
    Days.filter(_.date === date)
  }

  val compiledCategoryByDateAndName = Compiled { (date: Rep[DateTime], name: Rep[String]) =>
    val filteredByDate = Days.filter(_.date === date)
    for {
      data <- filteredByDate.map(t => (t.date, t.categories.arrayElements))
      _    <- filteredByDate if data._2 +>> "name" === name
    } yield data._2
  }

  def setUpDays()(implicit ctx: ExecutionContext) =
    setUp(Days, Days.filter(day => day.date < DateTime.now.minusDays(1)).delete)

  def saveDay(day: EventsDay): Future[Int] =
    db.run(Days.insertOrUpdate(day.copy(date = day.date.withTimeAtStartOfDay())))

  def findDay(date: DateTime)(implicit ctx: ExecutionContext): Future[Option[Day]] =
    db.run(compiledCategoryByDate(date.withTimeAtStartOfDay()).result.headOption.map(_.map(ed =>
      Day(Category.toCategoryList(ed.categories), ed.date))))

  def findCategory(date: DateTime, name: String)(implicit ctx: ExecutionContext): Future[Option[Category]] = db.run {
    compiledCategoryByDateAndName(date.withTimeAtStartOfDay(), name).result.headOption.map(_.map(Category.toCategory))
  }

  def findCategories(date: DateTime, names: List[String])(implicit ctx: ExecutionContext): Future[Seq[Category]] =
    db.run {
      val filteredByDate = Days.filter(_.date === date.withTimeAtStartOfDay())
      val category = for {
        data <- filteredByDate.map(t => (t.date, t.categories.arrayElements))
        _    <- filteredByDate if data._2 +>> "name" inSetBind names
      } yield data._2
      category.result.map(jsons => jsons.map(Category.toCategory))
    }

  def findCategories(startDate: DateTime, endDate: DateTime, names: List[String])(
      implicit ctx: ExecutionContext): Future[Seq[Category]] =
    db.run {
      val filteredByDate = Days.filter(r => r.date >= startDate && r.date <= endDate)
      val category = for {
        data <- filteredByDate.map(t => (t.date, t.categories.arrayElements))
        _    <- filteredByDate if data._2 +>> "name" inSetBind names
      } yield data._2
      category.result.map(_.map(Category.toCategory))
    }
}
