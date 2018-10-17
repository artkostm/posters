package com.artkostm.posters.worker.scraper

import cats.effect.Sync
import com.artkostm.posters.interfaces.event.Comment
import com.artkostm.posters.worker.config.ScraperConfig
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import org.joda.time.DateTime

abstract class Scraper[F[_]: Sync](config: ScraperConfig) {
  protected lazy val commentExtractor: HtmlExtractor[Element, List[Comment]] =
    _.map { comment =>
      Comment(comment >> text(config.tut.commentAuthorSelector),
        comment >> text(config.tut.commentDateSelector),
        comment >> text(config.tut.commentTextSelector),
        comment >?> text(config.tut.commentRatingSelector)
      )
    }.toList

  protected lazy val eventImagesExtractor: HtmlExtractor[Element, List[String]] =
    _.map(_ >> attr(config.tut.hrefAttrSelector)).toList

  def loadEvents(day: DateTime): F[Document]
  def load(link: String): F[Document]
}
