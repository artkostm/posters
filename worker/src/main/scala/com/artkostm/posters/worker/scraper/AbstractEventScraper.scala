package com.artkostm.posters.worker.scraper

import cats.effect.Sync
import cats.syntax.functor._
import com.artkostm.posters.interfaces.event.{Comment, EventData}
import com.artkostm.posters.interfaces.schedule._
import com.artkostm.posters.worker.config.ScraperConfig
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import org.joda.time.DateTime

abstract class AbstractEventScraper[F[_]: Sync](config: ScraperConfig) {
  private lazy val commentExtractor: HtmlExtractor[Element, List[Comment]] =
    _.map { comment =>
      Comment(
        comment >> text(config.tut.commentAuthorSelector),
        comment >> text(config.tut.commentDateSelector),
        comment >> text(config.tut.commentTextSelector),
        comment >?> text(config.tut.commentRatingSelector)
      )
    }.toList

  private lazy val eventImagesExtractor: HtmlExtractor[Element, List[String]] =
    _.map(_ >> attr(config.tut.hrefAttrSelector)).toList

  private lazy val eventExtractor: Element => List[Option[Event]] =
    _ >> elementList(config.tut.eventsSelector) map (event =>
      for {
        media <- event >?> element(config.tut.mediaSelector)
        name  <- event >?> element(config.tut.eventNameSelector)
        txt   <- event >?> element(config.tut.descriptionSelector)
      } yield
        Event(
          Media(
            media.attr(config.tut.hrefAttrSelector),
            media >> attr(config.tut.srcAttrSelector)(config.tut.imgSelector)
          ),
          name.text,
          Description(
            txt >> text(config.tut.descriptionTextSelector),
            txt >?> attr(config.tut.hrefAttrSelector)(config.tut.ticketSelector),
            (txt >?> text(config.tut.freeEventSelector)).isDefined
          )
        ))

  def eventInfo(link: String): F[Option[EventData]] =
    load(link) map { doc =>
      for {
        images              <- doc >?> extractor(config.tut.eventPhotoSelector, eventImagesExtractor)
        description: String = doc >> text(config.tut.eventDescriptionSelector)
        comments            <- doc >?> extractor(config.tut.commentsSelector, commentExtractor)
      } yield EventData(description, images, comments)
    }

  def event(day: DateTime): F[Day] =
    load(day) map { doc =>
      val categories = doc >> elementList(config.tut.blocksSelector) map { block =>
        Category(block >> text(config.tut.blockTitleSelector), eventExtractor(block).flatten)
      }
      Day(categories, day)
    }

  protected def load(day: DateTime): F[Document]
  protected def load(link: String): F[Document]
}
