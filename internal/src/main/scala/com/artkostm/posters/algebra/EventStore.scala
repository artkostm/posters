package com.artkostm.posters.algebra
import java.time.Instant

import com.artkostm.posters.interfaces.schedule.{Category, Day}

trait EventStore[F[_]] {
  def save(day: Day): F[Day]
  def findByDate(day: Instant): F[Option[Day]]
  def findByName(names: List[String]): F[List[Category]]
}
