package com.artkostm.posters.algebra

import cats.Foldable
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}

trait InfoStore[F[_]] {
  def deleteOld(): F[Int]
  def save(info: EventInfo): F[EventInfo]
  def save[K[_]: Foldable](info: K[(String, EventData, EventData)]): F[Int]
  def find(link: String): F[Option[EventInfo]]
}
