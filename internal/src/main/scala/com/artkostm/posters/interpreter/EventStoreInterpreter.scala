package com.artkostm.posters.interpreter

import java.time.Instant

import cats.data._
import cats.~>
import com.artkostm.posters.doobiemeta
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie._
import doobie.implicits._
import doobie.util.fragments

class EventStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends EventStore[F] {
  import doobiemeta._

  implicit val categoriesJsonValueCodec = JsonCodecMaker.make[List[Category]](CodecMakerConfig())
  implicit val categoryJsonValueCodec   = JsonCodecMaker.make[Category](CodecMakerConfig())

  implicit val han = LogHandler.jdkLogHandler

  override def save(day: Day): F[Day] =
    T(
      sql"""
           |INSERT INTO events ("date", "categories") VALUES (${day.date}, ${day.categories}) 
           |ON CONFLICT ON CONSTRAINT pk_events
           |DO 
           |UPDATE SET "categories"=${day.categories} """.stripMargin.update
        .withUniqueGeneratedKeys[Day]("date", "categories"))

  override def findByDate(day: Instant): F[Option[Day]] =
    T(sql"""SELECT "date", "categories" FROM events WHERE date=$day""".query[Day].option)

  override def findByName(names: NonEmptyList[String]): F[List[Category]] =
    T(
      (
        fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE """ ++ fragments.in(fr"""j->>'name'""",
                                                                                                    names)
      ).query[Category]
        .to[List])

  override def deleteOld(today: Instant): F[Int] =
    T(sql"""DELETE FROM events WHERE date < $today""".update.run)
}
