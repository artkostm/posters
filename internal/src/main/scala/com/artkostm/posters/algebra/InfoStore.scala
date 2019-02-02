package com.artkostm.posters.algebra
import com.artkostm.posters.interfaces.event.EventInfo

trait InfoStore[F[_]] {
  def save(info: EventInfo): F[EventInfo]
  def find(link: String): F[Option[EventInfo]]
}
