package com.artkostm.posters.worker.collector

import java.time.Instant
import java.time.temporal.ChronoUnit

import cats.implicits._
import cats.effect.{Concurrent, Effect, Timer}
import com.artkostm.posters.algebra.{EventStore, InfoStore}
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.worker.scraper.EventScraper
import fs2.Stream

import scala.concurrent.duration._
import doobie.implicits._

class EventCollector[F[_]: Timer: Concurrent](scraper: EventScraper[F],
                                              eventStore: EventStore[F],
                                              infoStore: InfoStore[F])(implicit F: Effect[F]) {
  def collect() = {
    val dayStream = Stream
      .range(-2, 31)
      .covary[F]
      .map(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(_, ChronoUnit.DAYS))
      .mapAsyncUnordered(4)(scraper.event)

    val insertDays = dayStream.mapAsyncUnordered(4)(eventStore.save)
    val saveEvents = dayStream
      .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
      .mapAsyncUnordered(4)(event => scraper.eventInfo(event.media.link))
      .mapAsync(4) {
        case Some(info) => infoStore.save(info)
        case None       => F.raiseError[EventInfo](new RuntimeException("cannot save empty event info"))
      }

    val graph = Stream(insertDays, saveEvents).parJoin(2)
    graph.merge(Stream.awakeEvery[F](24 hours) >> graph)
  }
}
