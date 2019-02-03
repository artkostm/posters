package com.artkostm.posters.interpreter

import com.artkostm.posters.doobiemeta
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import doobie.free.connection.ConnectionIO
import doobie.implicits._


class InfoStoreInterpreter extends InfoStore[ConnectionIO] {
    import doobiemeta._

    implicit val eventInfoJsonValueCodec = JsonCodecMaker.make[EventData](CodecMakerConfig())

    override def save(info: EventInfo): ConnectionIO[EventInfo] =
        sql"""insert into info ("link", "eventsInfo") values (${info.link}, ${info.eventInfo})"""
          .update
          .withUniqueGeneratedKeys[EventInfo]("link", "eventsInfo")

    override def find(link: String): ConnectionIO[Option[EventInfo]] =
        sql"""select "link", "eventsInfo" from info where link=$link"""
          .query[EventInfo]
          .option

    override def deleteOld(): ConnectionIO[Int] =
        sql"""
             |DELETE FROM info
             |WHERE NOT EXISTS (SELECT * FROM events WHERE categories::jsonb::text LIKE '%' || info.link || '%')
             |""".stripMargin.update.run
}
