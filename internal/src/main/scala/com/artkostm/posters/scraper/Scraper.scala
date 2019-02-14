package com.artkostm.posters.scraper

import java.time.Instant

import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Day

trait Scraper[F[_]] {
  def event(day: Instant): F[Day]
  def eventInfo(link: String): F[Option[EventInfo]]
}
