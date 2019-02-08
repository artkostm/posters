package com.artkostm.posters.interpreter

import java.time.Instant

import cats.{Monad, ~>}
import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.interfaces.intent.Intent
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

class VisitorStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends VisitorStore[F] {
  override def deleteOld(today: Instant): F[Int] =
    T(sql"""DELETE FROM visitors WHERE date < $today""".update.run)

  override def save(intent: Intent): F[Intent] =
    T(
      sql"""INSERT INTO visitors ("date", "event_name", "vids", "uids") (${intent.date}, ${intent.event_name}, '{}', '{}')""".update
        .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids"))

  override def find(date: Instant, eventName: String): F[Option[Intent]] =
    T(
      sql"""SELECT "date", event_name, vids, uids FROM visitors WHERE "date" = $date AND event_name = $eventName"""
        .query[Intent]
        .option)

  override def asVolunteer(id: String, intent: Intent): F[Intent] =
    T(
      sql"""UPDATE visitors SET vids = array_append(vids, $id) WHERE event_name = ${intent.event_name} AND "date" = ${intent.date}""".update
        .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids"))

  override def asPlainUser(id: String, intent: Intent): F[Intent] =
    T(
      sql"""UPDATE visitors SET uids = array_append(uids, $id) WHERE event_name = ${intent.event_name} AND "date" = ${intent.date}""".update
        .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids"))
}
