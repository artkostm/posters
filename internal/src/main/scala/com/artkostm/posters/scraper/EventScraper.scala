package com.artkostm.posters.scraper

import java.time.LocalDate

import cats.effect.Sync
import com.artkostm.posters.Configuration.ScraperConfig
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Day
import fs2._

trait Scraper[F[_]] {
  def event(day: LocalDate): F[Day]
  def eventInfo(link: String): F[Option[EventInfo]]

  override def toString: String = "Scrapper#" + this.hashCode()
}
