package com.artkostm.posters.worker.collector

import java.time.Instant
import java.time.temporal.ChronoUnit

import cats.effect.{Concurrent, Timer}
import com.artkostm.posters.algebra.{EventStore, InfoStore, VisitorStore}
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.scraper.Scraper
import fs2.Stream

import scala.concurrent.duration._

class EventCollector[F[_]: Timer](scraper: Scraper[F],
                                  eventStore: EventStore[F],
                                  infoStore: InfoStore[F],
                                  visitorStore: VisitorStore[F])(implicit F: Concurrent[F]) {
  def collect() = {
    val dayStream = Stream
      .range(-2, 31)
      .covary[F]
      .map(Instant.now().plus(_, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
      .mapAsyncUnordered(4)(scraper.event)

    val insertDays = dayStream.mapAsyncUnordered(4)(eventStore.save)
    val saveEvents = dayStream
      .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
      .mapAsyncUnordered(4)(event => scraper.eventInfo(event.media.link))
      //      .filter {
      //        case Some(_) => true
      //        case _       => false
      //      }
      .chunkN(50)
//      .switchMap()
      //.map(chunk => chunk.toList.flatten)
      .mapAsync(4)(chunk => {println("SAVING CHUNK: " + chunk); infoStore.save(chunk.toList.flatten)})

    val graph = Stream.eval(eventStore.deleteOld(Instant.now())) >>
      Stream.eval(infoStore.deleteOld()) >>
      Stream.eval(visitorStore.deleteOld(Instant.now())) >>
      Stream(insertDays, saveEvents).parJoin(2)
    graph//.merge(Stream.awakeEvery[F](24 hours) >> graph)
  }
}
