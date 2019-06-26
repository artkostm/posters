package com.artkostm.posters.worker.collector

import java.time.LocalDate

import cats.data.NonEmptyList
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import com.artkostm.posters.algebra.{EventStore, InfoStore, VisitorStore}
import com.artkostm.posters.logging.mdcF
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.scraper.EventScraper
import fs2._
import org.log4s.getLogger

class EventCollector[F[_]: Timer](scrapers: NonEmptyList[EventScraper[F]],
                                  eventStore: EventStore[F],
                                  infoStore: InfoStore[F],
                                  visitorStore: VisitorStore[F])(implicit F: Concurrent[F]) {
  private[this] val logger = getLogger("Collector")

  // TODO: move all magic numbers to config
  def collect(days: Seq[LocalDate]) = {

    def collectWith(scraper: EventScraper[F], days: Seq[LocalDate]): Stream[F, Any] = {
      val dayStream = Stream
        .emits(days)
        .covary[F]
        .mapAsyncUnordered(4)(scraper.event(_))

      val insertDays = dayStream
        .mapAsyncUnordered(4) { day =>
          eventStore.save(day) <* mdcF(
            "Day" -> s"""{"date":"${day.eventDate}", "categories":[${day.categories
              .map(_.name)
              .mkString("\"", "\",\"", "\"")}]}"""
          )(logger.info("Saving day."))
        }
        .handleErrorWith { error =>
          Stream.eval[F, Unit](mdcF() {
            logger.error(error)("Error while saving days.")
          }) >> Stream.empty
        }
      val saveEvents = dayStream
        .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
        .mapAsyncUnordered(4)(event => scraper.eventInfo(event.media.link))
        .chunkN(50)
        .mapAsync(4)(chunk =>
          infoStore.save(chunk.map {
            case Some(EventInfo(link, eventInfo)) => (link, eventInfo, eventInfo)
          }) <* mdcF(
            "EventChunk" -> s"""{"chunk_size":"${chunk.size}"}"""
          )(logger.info("Saving event info chunk.")))
        .handleErrorWith { error =>
          Stream.eval[F, Unit](mdcF() {
            logger.error(error)("Error while saving event info.")
          }) >> Stream.empty
        }

      Stream.eval(eventStore.deleteOld(LocalDate.now())) >>
        Stream.eval(infoStore.deleteOld()) >>
        Stream.eval(visitorStore.deleteOlderThan(LocalDate.now())) >>
        Stream(insertDays, saveEvents).parJoin(2)
    }

    for {
      scraper <- Stream.emits[F, EventScraper[F]](scrapers.toList)
      _       <- collectWith(scraper, days)
    } yield ()
  }
}
