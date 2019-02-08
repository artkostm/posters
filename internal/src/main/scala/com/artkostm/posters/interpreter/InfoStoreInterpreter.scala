package com.artkostm.posters.interpreter

import cats.~>
import com.artkostm.posters.doobiemeta
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie._
import doobie.implicits._

class InfoStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends InfoStore[F] {
  import doobiemeta._

  implicit val eventInfoJsonValueCodec = JsonCodecMaker.make[EventData](CodecMakerConfig())

  override def save(info: EventInfo): F[EventInfo] =
    T(
      sql"""
           |INSERT INTO info ("link", "eventsInfo") VALUES (${info.link}, ${info.eventInfo})
           |ON CONFLICT ON CONSTRAINT pk_info
           |DO
           |UPDATE SET "eventsInfo"=${info.eventInfo}""".stripMargin.update
        .withUniqueGeneratedKeys[EventInfo]("link", "eventsInfo"))

  override def find(link: String): F[Option[EventInfo]] =
    T(
      sql"""select "link", "eventsInfo" from info where link=$link"""
        .query[EventInfo]
        .option)

  override def deleteOld(): F[Int] =
    T(sql"""
             |DELETE FROM info
             |WHERE NOT EXISTS (SELECT * FROM events WHERE categories::jsonb::text LIKE '%' || info.link || '%')
             |""".stripMargin.update.run)
}
