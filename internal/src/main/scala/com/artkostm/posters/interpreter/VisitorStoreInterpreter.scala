package com.artkostm.posters.interpreter

import java.time.Instant

import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.interfaces.intent.Intent
import doobie.free.connection.ConnectionIO
import doobie.implicits._

class VisitorStoreInterpreter extends VisitorStore[ConnectionIO] {
  override def deleteOld(today: Instant): ConnectionIO[Int] =
    sql"""DELETE FROM visitors WHERE date < $today""".update.run

  override def save(intent: Intent): ConnectionIO[Intent] =
    sql"""INSERT INTO visitors ("date", event_name, vids, uids) (${intent.date}, ${intent.eventName}, '{}', '{}')""".update
      .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids")

  override def find(date: Instant, eventName: String): ConnectionIO[Option[Intent]] =
    sql"""SELECT "date", event_name, vids, uids FROM visitors WHERE "date" = $date AND event_name = $eventName"""
      .query[Intent]
      .option

  override def asVolunteer(id: String, intent: Intent): ConnectionIO[Intent] =
    sql"""UPDATE visitors SET vids = array_append(vids, $id) WHERE event_name = ${intent.eventName} AND "date" = ${intent.date}""".update
      .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids")

  override def asPlainUser(id: String, intent: Intent): ConnectionIO[Intent] =
    sql"""UPDATE visitors SET uids = array_append(uids, $id) WHERE event_name = ${intent.eventName} AND "date" = ${intent.date}""".update
      .withUniqueGeneratedKeys[Intent]("date", "event_name", "vids", "uids")
}
