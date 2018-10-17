package com.artkostm.posters.worker.scraper

import cats.effect.Sync
import cats.syntax.functor._
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.worker.config.ScraperConfig
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import org.joda.time.DateTime

class EventInfoScraper[F[_]](config: ScraperConfig)(implicit F: Sync[F]) extends Scraper(config) {
  def event(link: String): F[Option[EventInfo]] =
    load(link) map { doc =>
      for {
        images <- doc >?> extractor(config.tut.eventPhotoSelector, eventImagesExtractor)
        description: String = doc >> text(config.tut.eventDescriptionSelector)
        comments <- doc >?> extractor(config.tut.commentsSelector, commentExtractor)
      } yield EventInfo(description, images, comments)
  }

  override def loadEvents(day: DateTime) = load(s"${config.tut.url}${config.tut.format.print(day)}")
  override def load(link: String) = F.delay { new JsoupBrowser().get(link) }
}
