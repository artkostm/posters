package com.artkostm.posters.interpreter

import java.time.LocalDate

import cats.data._
import cats.~>
import com.artkostm.posters.doobiemeta._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import doobie._
import doobie.implicits._
import doobie.util.fragments

class EventStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends EventStore[F] {
  import EventStoreInterpreter._

  implicit val dayJsonValueCodec = dayCodec

  override def save(day: Day): F[Day] =
    T(saveEvents(day).withUniqueGeneratedKeys[Day]("eventdate", "categories"))

  override def findByDate(day: LocalDate): F[Option[Day]] =
    T(sql"""SELECT "eventdate", "categories" FROM events e WHERE e.eventdate=$day""".query[Day].option)

  override def findByNames(names: NonEmptyList[String]): F[List[Category]] =
    T(
      (
        fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE """ ++ fragments.in(fr"""j->>'name'""",
                                                                                                    names)
      ).query[Category]
        .to[List])

  override def findByNamesAndPeriod(names: NonEmptyList[String], start: LocalDate, end: LocalDate): F[List[Category]] =
    T(
      (
        fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE t.eventdate > $start AND t.eventdate < $end AND""" ++ fragments
          .in(fr"""j->>'name'""", names)
      ).query[Category]
        .to[List])

  override def findByNameAndDate(name: String, date: LocalDate): F[Option[Category]] =
    T(
      sql"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE j->>'name' = $name AND t.eventdate = $date"""
        .query[Category]
        .option)

  override def deleteOld(today: LocalDate): F[Int] =
    T(sql"""DELETE FROM events WHERE events.eventdate < $today""".update.run)

}

private object EventStoreInterpreter {
  implicit val han = LogHandler.jdkLogHandler

  def saveEvents(day: Day): Update0 =
    sql"""INSERT INTO events ("eventdate", "categories") VALUES (${day.eventDate}, ${day.categories})
         ON CONFLICT ON CONSTRAINT pk_events DO UPDATE SET categories=${day.categories} """.update
}
