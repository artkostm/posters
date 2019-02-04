package com.artkostm.posters.algebra

import java.time.Instant

import com.artkostm.posters.interfaces.intent.Intent

trait VisitorStore[F[_]] {
  def deleteOld(today: Instant): F[Int]
  def save(intent: Intent): F[Intent]
  def find(date: Instant, eventName: String): F[Option[Intent]]
  def asVolunteer(id: String, intent: Intent): F[Intent]
  def asPlainUser(id: String, intent: Intent): F[Intent]
}
