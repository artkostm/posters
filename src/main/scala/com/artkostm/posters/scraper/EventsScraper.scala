package com.artkostm.posters.scraper

import com.artkostm.posters.model._
import com.artkostm.posters.modules.ScraperConfig
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._
import org.joda.time.DateTime

class EventsScraper(config: ScraperConfig) {
  private val format = config.tut.format
  private val browser = JsoupBrowser()

  def scheduleFor(day: DateTime): Day = {
    val doc = loadDocument(day)
    val blocks = doc >> elementList(config.tut.blocksSelector)

    val categories = for {
      block <- blocks
    } yield Category(block >> text(config.tut.blockTitleSelector), extractEvents(block).flatten)

    Day(categories, day.toDate)
  }
  
  def eventInfo(link: String): Option[Info] = {
    val doc = loadDocument(link)
    for {
      images <- doc >?> elementList(config.tut.eventPhotoSelector) >> attr(config.tut.hrefAttrSelector)
      description: String = doc >> text(config.tut.eventDescriptionSelector)
      comments <- doc >?> elementList(config.tut.commentsSelector)
    } yield Info(link, EventInfo(description, images, extractComments(comments)))
  }

  def loadDocument(day: DateTime): Document = browser.get(s"${config.tut.url}${format.print(day)}")
  def loadDocument(link: String): Document = browser.get(link)

  private[this] def extractEvents(block: Element): List[Option[Event]] = {
    block >> elementList(config.tut.eventsSelector) map { event =>
      for {
        media <- event >?> element(config.tut.mediaSelector)
        name <- event >?> element(config.tut.eventNameSelector)
        txt <- event >?> element(config.tut.descriptionSelector)
      } yield Event(
        Media(media.attr(config.tut.hrefAttrSelector),
          media >> attr(config.tut.srcAttrSelector)(config.tut.imgSelector)),
        name.text,
        Description(txt >> text(config.tut.descriptionTextSelector),
          txt >?> attr(config.tut.hrefAttrSelector)(config.tut.ticketSelector),
          (txt >?> text(config.tut.freeEventSelector)).isDefined)
      )
    }
  }
  
  private[this] def extractComments(comments: List[Element]): List[Comment] =
    comments map { comment =>
      Comment(comment >> text(config.tut.commentAuthorSelector),
        comment >> text(config.tut.commentDateSelector),
        comment >> text(config.tut.commentTextSelector))
    }
}
