package com.artkostm.posters.worker.scraper

import java.time.Instant

import cats.effect.Sync
import cats.syntax.functor._
import com.artkostm.posters.worker.config.ScraperConfig
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

class EventScraper[F[_]](config: ScraperConfig)(implicit F: Sync[F]) extends AbstractEventScraper(config) {
  private lazy val browser = F.delay(new JsoupBrowser())

  override protected def load(day: Instant) = load(s"${config.tut.url}${config.tut.format.format(day)}")
  override protected def load(link: String)  = browser.map(_.get(link))
}
