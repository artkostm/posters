package com.artkostm.posters.interpreter

import java.time.Instant

import cats.data._
import cats.~>
import com.artkostm.posters.doobiemeta._
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie._
import doobie.implicits._
import doobie.util.fragments

class EventStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends EventStore[F] {
  import EventStoreInterpreter._

  implicit val dayCodec = dayJsonValueCodec

  override def save(day: Day): F[Day] =
    T(saveEvents(day).withUniqueGeneratedKeys[Day]("date", "categories"))

  override def findByDate(day: Instant): F[Option[Day]] =
    T(sql"""SELECT "date", "categories" FROM events WHERE date=$day""".query[Day].option)

  override def findByNames(names: NonEmptyList[String]): F[List[Category]] =
    T(
      (
        fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE """ ++ fragments.in(fr"""j->>'name'""",
                                                                                                    names)
      ).query[Category]
        .to[List])

  override def findByNamesAndPeriod(names: NonEmptyList[String], start: Instant, end: Instant): F[List[Category]] =
    T(
      (
        fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE t.date > $start AND t.date < $end AND""" ++ fragments
          .in(fr"""j->>'name'""", names)
      ).query[Category]
        .to[List])

  override def findByNameAndDate(name: String, date: Instant): F[Option[Category]] =
    T(
      sql"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE j->>'name' = $name AND t.date = $date"""
        .query[Category]
        .option)

  override def deleteOld(today: Instant): F[Int] =
    T(sql"""DELETE FROM events WHERE date < $today""".update.run)

}

private object EventStoreInterpreter {
  implicit val categoriesJsonValueCodec = JsonCodecMaker.make[List[Category]](CodecMakerConfig())
  implicit val categoryJsonValueCodec   = JsonCodecMaker.make[Category](CodecMakerConfig())
  implicit val dayJsonValueCodec        = JsonCodecMaker.make[Day](CodecMakerConfig())

  implicit val han = LogHandler.jdkLogHandler

  def saveEvents(day: Day): Update0 =
    sql"""INSERT INTO events ("date", "categories") VALUES (${day.date}, ${day.categories})
         ON CONFLICT ON CONSTRAINT pk_events DO UPDATE SET "categories"=${day.categories} """.update
}
