package com.artkostm.posters.interpreter

import java.time.Instant

import cats.~>
import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

class VisitorStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends VisitorStore[F] {
  import VisitorSQL._

  override def deleteOld(today: Instant): F[Int] =
    T(deleteOldIntents(today).run)

  override def save(intent: Intent): F[Intents] =
    T(saveIntent(intent).withUniqueGeneratedKeys[Intents]("date", "eventName", "vids", "uids"))

  override def find(date: Instant, eventName: String): F[Option[Intents]] =
    T(findByDateAndName(date, eventName).option)

  override def asVolunteer(intent: Intent): F[Intents] =
    T(volunteer(intent).withUniqueGeneratedKeys[Intents]("date", "eventName", "vids", "uids"))

  override def asPlainUser(intent: Intent): F[Intents] =
    T(plainUser(intent).withUniqueGeneratedKeys[Intents]("date", "eventName", "vids", "uids"))

  override def leave(intent: Intent): F[Intents] =
    T(leaveEvent(intent).withUniqueGeneratedKeys[Intents]("date", "eventName", "vids", "uids"))
}

private object VisitorSQL {
  def findByDateAndName(date: Instant, eventName: String): Query0[Intents] =
    sql"""SELECT "date", eventName, vids, uids FROM visitors WHERE "date" = $date AND eventName = $eventName"""
      .query[Intents]

  def saveIntent(intent: Intent): Update0 =
    sql"""
         INSERT INTO visitors ("date", "eventName", "vids", "uids")
         (${intent.date}, ${intent.eventName}, '{}', '{}')""".update

  def deleteOldIntents(today: Instant): Update0 =
    sql"""DELETE FROM visitors WHERE date < $today""".update

  def leaveEvent(intent: Intent): Update0 =
    sql"""
         UPDATE visitors
         SET uids = array_remove(uids, ${intent.userId}), vids = array_remove(vids, ${intent.userId})
         WHERE eventName = ${intent.eventName} AND "date" = ${intent.date}
         """.update

  def whereRf(intent: Intent) = fr"""WHERE eventName = ${intent.eventName} AND "date" = ${intent.date}"""

  def createOrUpdate(intent: Intent, insertFr: Fragment, updateFr: Fragment): Fragment =
    fr"""INSERT INTO visitors ("date", "eventName", "vids", "uids")""" ++ insertFr ++
      fr"ON CONFLICT ON CONSTRAINT pk_visitors DO UPDATE visitors SET" ++ updateFr ++
      whereRf(intent)

  def volunteer(intent: Intent): Update0 =
    createOrUpdate(intent,
      fr"(${intent.date}, ${intent.eventName}, '{${intent.userId}}', '{}')",
      fr"vids = array_append(vids, ${intent.userId})").update

  def plainUser(intent: Intent): Update0 =
    createOrUpdate(intent,
      fr"(${intent.date}, ${intent.eventName}, '{}', '{${intent.userId}}')",
      fr"uids = array_append(uids, ${intent.userId})").update
}