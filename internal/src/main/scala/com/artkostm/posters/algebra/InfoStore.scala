package com.artkostm.posters.algebra

import com.artkostm.posters.interfaces.event.EventInfo

trait InfoStore[F[_]] {
  def deleteOld(): F[Int]
  def save(info: EventInfo): F[EventInfo]
  def save(info: List[EventInfo]): F[Int]
  def find(link: String): F[Option[EventInfo]]
}
