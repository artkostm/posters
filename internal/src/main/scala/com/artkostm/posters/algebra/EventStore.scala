package com.artkostm.posters.algebra
import java.time.Instant

import cats.data.NonEmptyList
import com.artkostm.posters.interfaces.schedule.{Category, Day}

trait EventStore[F[_]] {
  def deleteOld(today: Instant): F[Int]
  def save(day: Day): F[Day]
  def findByDate(day: Instant): F[Option[Day]]
  def findByName(names: NonEmptyList[String]): F[List[Category]]
}
