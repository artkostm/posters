package com.artkostm.posters.interpreter

import java.time.Instant

import cats.data._
import com.artkostm.posters.doobiemeta
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.fragments

class EventStoreInterpreter extends EventStore[ConnectionIO] {
  import doobiemeta._

  implicit val categoriesJsonValueCodec = JsonCodecMaker.make[List[Category]](CodecMakerConfig())
  implicit val categoryJsonValueCodec   = JsonCodecMaker.make[Category](CodecMakerConfig())

  override def save(day: Day): ConnectionIO[Day] =
    sql"""INSERT INTO events ("date", "categories") VALUES (${day.date}, ${day.categories})""".update
      .withUniqueGeneratedKeys[Day]("date", "categories")

  override def findByDate(day: Instant): ConnectionIO[Option[Day]] =
    sql"""SELECT "date", "categories" FROM events WHERE date=$day""".query[Day].option

  override def findByName(names: NonEmptyList[String]): ConnectionIO[List[Category]] =
    (
      fr"""SELECT j FROM events t, jsonb_array_elements(t.categories) j WHERE """ ++ fragments.in(fr"""j->>'name'""",
                                                                                                  names)
    ).query[Category]
      .to[List]

  override def deleteOld(today: Instant): ConnectionIO[Int] =
    sql"""DELETE FROM events WHERE date < $today""".update.run
}
