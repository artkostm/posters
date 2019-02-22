package com.artkostm.posters.interpreter

import java.time.Instant

import cats.~>
import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

class VisitorStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends VisitorStore[F] {
  override def deleteOld(today: Instant): F[Int] =
    T(sql"""DELETE FROM visitors WHERE date < $today""".update.run)

  override def save(intent: Intent): F[Intents] =
    T(
      sql"""INSERT INTO visitors ("date", "event_name", "vids", "uids") (${intent.date}, ${intent.eventName}, '{}', '{}')""".update
        .withUniqueGeneratedKeys[Intents]("date", "event_name", "vids", "uids"))

  override def find(date: Instant, eventName: String): F[Option[Intents]] =
    T(
      sql"""SELECT "date", event_name, vids, uids FROM visitors WHERE "date" = $date AND event_name = $eventName"""
        .query[Intents]
        .option)

  override def asVolunteer(intent: Intent): F[Intents] =
    T(
      sql"""UPDATE visitors SET vids = array_append(vids, ${intent.userId}) WHERE event_name = ${intent.eventName} AND "date" = ${intent.date}""".update
        .withUniqueGeneratedKeys[Intents]("date", "event_name", "vids", "uids"))

  override def asPlainUser(intent: Intent): F[Intents] =
    T(
      sql"""UPDATE visitors SET uids = array_append(uids, ${intent.userId}) WHERE event_name = ${intent.eventName} AND "date" = ${intent.date}""".update
        .withUniqueGeneratedKeys[Intents]("date", "event_name", "vids", "uids"))
}
