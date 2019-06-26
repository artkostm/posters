package com.artkostm.posters.scraper

import java.time.LocalDate

import cats.effect.Sync
import cats.implicits._
import com.artkostm.posters.Configuration.ScraperConfig
import com.artkostm.posters.logging.mdcF
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.log4s.getLogger

class AfishaEventScraper[F[_]](config: ScraperConfig)(implicit F: Sync[F]) extends AbstractAfishaEventScraper(config) {
  private[this] val logger = getLogger("AfishaScraper")
  private lazy val browser = F.delay(new JsoupBrowser())

  override protected def load(day: LocalDate) =
    mdcF()(logger.debug(s"Scraping events [$day].")) *> load(s"${config.tut.url}${config.tut.format.format(day)}")

  override protected def load(link: String) =
    mdcF()(logger.debug(s"Scraping event info from $link.")) *> browser.map(_.get(link))
}
