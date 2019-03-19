package com.artkostm.posters.scraper

import java.time.Instant

import cats.effect.Sync
import com.artkostm.posters.Configuration.ScraperConfig
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Day
import fs2._

trait Scraper[F[_]] {
  def event(day: Instant): F[Day]
  def eventInfo(link: String): F[Option[EventInfo]]
}

object Scraper {
  def apply[F[_]: Sync](config: ScraperConfig): Stream[F, Scraper[F]] =
    Stream(new AfishaScraper[F](config), new FailedScraper[F])
}

class FailedScraper[F[_]: Sync] extends Scraper[F] {
  override def event(day: Instant): F[Day] = Sync[F].raiseError(new RuntimeException("Failed event for day " + day))
  override def eventInfo(link: String): F[Option[EventInfo]] =
    Sync[F].raiseError(new RuntimeException("Failed event info for " + link))
}
