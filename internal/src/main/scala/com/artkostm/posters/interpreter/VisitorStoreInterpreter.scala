package com.artkostm.posters.interpreter

import java.time.LocalDate

import cats.~>
import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

class VisitorStoreInterpreter[F[_]](T: ConnectionIO ~> F) extends VisitorStore[F] {
  import VisitorStoreInterpreter._

  override def deleteOld(today: LocalDate): F[Int] =
    T(deleteOldIntents(today).run)

  override def save(intent: Intent): F[Intents] =
    T(saveIntent(intent).withUniqueGeneratedKeys[Intents]("eventdate", "eventname", "vids", "uids"))

  override def find(date: LocalDate, eventName: String): F[Option[Intents]] =
    T(findByDateAndName(date, eventName).option)

  override def asVolunteer(intent: Intent): F[Intents] =
    T(volunteer(intent).withUniqueGeneratedKeys[Intents]("eventdate", "eventname", "vids", "uids"))

  override def asPlainUser(intent: Intent): F[Intents] =
    T(plainUser(intent).withUniqueGeneratedKeys[Intents]("eventdate", "eventname", "vids", "uids"))

  override def leave(intent: Intent): F[Intents] =
    T(leaveEvent(intent).withUniqueGeneratedKeys[Intents]("eventdate", "eventname", "vids", "uids"))
}

private object VisitorStoreInterpreter {
  implicit val han = LogHandler.jdkLogHandler

  def findByDateAndName(date: LocalDate, eventName: String): Query0[Intents] =
    sql"""SELECT "eventdate", eventname, vids, uids FROM visitors WHERE eventdate = $date AND eventname = $eventName"""
      .query[Intents]

  def saveIntent(intent: Intent): Update0 =
    sql"""
         INSERT INTO visitors ("eventdate", "eventname", "vids", "uids") VALUES
         (${intent.eventDate}, ${intent.eventName}, '{}', '{}')""".update

  def deleteOldIntents(today: LocalDate): Update0 =
    sql"""DELETE FROM visitors WHERE eventdate < $today""".update

  def leaveEvent(intent: Intent): Update0 =
    sql"""
         UPDATE visitors
         SET uids = array_remove(uids, ${intent.userId}), vids = array_remove(vids, ${intent.userId})
         WHERE eventname = ${intent.eventName} AND eventdate = ${intent.eventDate}
         """.update

  def whereFr(intent: Intent) = fr"""WHERE eventname = ${intent.eventName} AND eventdate = ${intent.eventDate}"""

  def createOrUpdate(intent: Intent, insertFr: Fragment, updateFr: Fragment): Fragment =
    fr"""INSERT INTO visitors ("eventdate", "eventname", "vids", "uids")""" ++ insertFr ++
      fr"ON CONFLICT ON CONSTRAINT pk_visitors DO UPDATE visitors SET" ++ updateFr ++
      whereFr(intent)

  def volunteer(intent: Intent): Update0 =
    createOrUpdate(intent,
      fr"(${intent.eventDate}, ${intent.eventName}, '{${intent.userId}}', '{}')",
      fr"vids = array_append(vids, ${intent.userId})").update

  def plainUser(intent: Intent): Update0 =
    createOrUpdate(intent,
      fr"(${intent.eventDate}, ${intent.eventName}, '{}', '{${intent.userId}}')",
      fr"uids = array_append(uids, ${intent.userId})").update
}