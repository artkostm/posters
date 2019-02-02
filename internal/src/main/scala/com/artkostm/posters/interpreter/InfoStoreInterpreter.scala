package com.artkostm.posters.interpreter

import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.EventInfo
import doobie.free.connection.ConnectionIO
import doobie.implicits._


class InfoStoreInterpreter extends InfoStore[ConnectionIO] {
    override def save(info: EventInfo): ConnectionIO[EventInfo] =
        sql"""insert into info ("link", "eventsInfo") values (${info.link}, ${info.eventInfo})"""
          .update
          .withUniqueGeneratedKeys[EventInfo]("link", "eventsInfo")

    override def find(link: String): ConnectionIO[Option[EventInfo]] =
        sql"""select link, eventsInfo from info where link=$link"""
          .query[EventInfo]
          .option
}
