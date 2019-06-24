package com.artkostm.posters.interpreter

import cats.{Foldable, ~>}
import com.artkostm.posters.doobiemeta._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.artkostm.posters.logging
import doobie._
import doobie.implicits._

class InfoStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends InfoStore[F] {
  import InfoStoreInterpreter._

  override def save(info: EventInfo): F[EventInfo] =
    T(saveInfo(info).withUniqueGeneratedKeys[EventInfo]("link", "eventInfo"))

  override def find(link: String): F[Option[EventInfo]] =
    T(findByLink(link).option)

  override def deleteOld(): F[Int] =
    T(deleteOldInfo().run)

  override def save[K[_]: Foldable](info: K[(String, EventData, EventData)]): F[Int] =
    T(Update[(String, EventData, EventData)](bulkUpsert).updateMany(info))
}

object InfoStoreInterpreter {
  implicit val han = logging.doobieLogHandler

  implicit val eventInfoJsonValueCodec = eventInfoCodec

  def saveInfo(info: EventInfo): Update0 =
    sql"""
         INSERT INTO info ("link", "eventInfo") VALUES (${info.link}, ${info.eventInfo})
         ON CONFLICT ON CONSTRAINT pk_info DO
         UPDATE SET "eventInfo"=${info.eventInfo}""".update

  def findByLink(link: String): Query0[EventInfo] =
    sql"""SELECT "link", "eventInfo" FROM info WHERE link=$link"""
      .query[EventInfo]

  def deleteOldInfo(): Update0 =
    sql"""DELETE FROM info
         WHERE NOT EXISTS (SELECT * FROM events WHERE categories::jsonb::text LIKE '%' || info.link || '%')""".update

  val bulkUpsert =
    """INSERT INTO info ("link", "eventInfo") VALUES (?, ?)
      ON CONFLICT ON CONSTRAINT pk_info DO UPDATE SET "eventInfo"=?"""
}