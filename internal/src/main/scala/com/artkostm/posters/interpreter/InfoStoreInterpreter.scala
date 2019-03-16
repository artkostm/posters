package com.artkostm.posters.interpreter

import cats.{Foldable, ~>}
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
           |INSERT INTO info ("link", "eventInfo") VALUES (${info.link}, ${info.eventInfo})
           |ON CONFLICT ON CONSTRAINT pk_info
           |DO
           |UPDATE SET "eventInfo"=${info.eventInfo}""".stripMargin.update
        .withUniqueGeneratedKeys[EventInfo]("link", "eventsInfo"))

  override def find(link: String): F[Option[EventInfo]] =
    T(
      sql"""select "link", "eventInfo" from info where link=$link"""
        .query[EventInfo]
        .option)

  override def deleteOld(): F[Int] =
    T(sql"""
             |DELETE FROM info
             |WHERE NOT EXISTS (SELECT * FROM events WHERE categories::jsonb::text LIKE '%' || info.link || '%')
             |""".stripMargin.update.run)

  override def save[K[_]: Foldable](info: K[(String, EventData, EventData)]): F[Int] =
    T(
      Update[(String, EventData, EventData)](
        """
           INSERT INTO info ("link", "eventInfo") VALUES (?, ?)
           ON CONFLICT ON CONSTRAINT pk_info
           DO
           UPDATE SET "eventInfo"=?"""
      ).updateMany(info))
}
