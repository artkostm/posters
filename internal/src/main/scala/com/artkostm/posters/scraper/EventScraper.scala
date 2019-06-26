package com.artkostm.posters.scraper

import java.time.LocalDate

import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Day

trait EventScraper[F[_]] {
  def event(day: LocalDate): F[Day]
  def eventInfo(link: String): F[Option[EventInfo]]
}
