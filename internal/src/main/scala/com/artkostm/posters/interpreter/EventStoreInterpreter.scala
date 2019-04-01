package com.artkostm.posters.interpreter

import java.time.LocalDate

import doobie._
import doobie.implicits._
import doobie.util.fragments
import cats.data._
import cats.~>
import com.artkostm.posters.doobiemeta._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec

/**
  * Doobie interpreter for EventStore
  * @param T is a functor transformation from `ConnectionIO` to `F`
  * @tparam F
  */
class EventStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends EventStore[F] {
  import EventStoreInterpreter._

  implicit val dayJsonValueCodec: JsonValueCodec[Day] = dayCodec

  override def save(day: Day): F[Day] =
    T(saveEvents(day).withUniqueGeneratedKeys[Day]("eventdate", "categories"))

  override def findByDate(day: LocalDate): F[Option[Day]] =
    T(findByDay(day).option)

  override def findByNames(names: NonEmptyList[String]): F[List[Category]] =
    T(findCategoriesByNames(names).to[List])

  override def findByNamesAndPeriod(names: NonEmptyList[String], start: LocalDate, end: LocalDate): F[List[Category]] =
    T(findCategoriesByNamesAndPeriod(names, start, end).to[List])

  override def findByNameAndDate(name: String, date: LocalDate): F[Option[Category]] =
    T(findCategoryByNameAndDate(name, date).option)

  override def deleteOld(today: LocalDate): F[Int] =
    T(deleteOldEvents(today).run)
}

/**
  * Companion object with sql staff
  */
private object EventStoreInterpreter {
  implicit val han = LogHandler.jdkLogHandler

  implicit val readDay = Read[(LocalDate, List[Category])].map { case (date, categories) => Day(date, categories) }

  def saveEvents(day: Day): Update0 =
    sql"""INSERT INTO events ("eventdate", "categories") VALUES (${day.eventDate}, ${day.categories})
         ON CONFLICT ON CONSTRAINT pk_events DO UPDATE SET categories=${day.categories} """.update

  def findByDay(day: LocalDate): Query0[Day] =
    sql"""SELECT "eventdate", "categories" FROM events e WHERE e.eventdate=$day""".query[Day]

  def findCategories(where: Fragment): Query0[Category] =
    (fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE""" ++ where).query[Category]

  def findCategoriesByNames(names: NonEmptyList[String]): Query0[Category] =
    findCategories(fragments.in(fr"""j->>'name'""", names))

  def findCategoriesByNamesAndPeriod(names: NonEmptyList[String], start: LocalDate, end: LocalDate): Query0[Category] =
    findCategories(fr"t.eventdate > $start AND t.eventdate < $end AND" ++ fragments.in(fr"""j->>'name'""", names))

  def findCategoryByNameAndDate(name: String, date: LocalDate): Query0[Category] =
    findCategories(fr"j->>'name' = $name AND t.eventdate = $date")

  def deleteOldEvents(today: LocalDate): Update0 =
    sql"""DELETE FROM events WHERE events.eventdate < $today""".update
}
