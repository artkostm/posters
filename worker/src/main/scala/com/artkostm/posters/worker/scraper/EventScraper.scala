package com.artkostm.posters.worker.scraper

import cats.effect.Sync
import cats.syntax.functor._
import com.artkostm.posters.worker.config.ScraperConfig
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.joda.time.DateTime

class EventScraper[F[_]](config: ScraperConfig)(implicit F: Sync[F]) extends AbstractEventScraper(config) {
  private lazy val browser = F.delay(new JsoupBrowser())

  override protected def load(day: DateTime) = load(s"${config.tut.url}${config.tut.format.print(day)}")
  override protected def load(link: String)  = browser.map(_.get(link))
}
