package com.artkostm.posters.worker

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.time.temporal.ChronoUnit
import java.util.Locale

import fs2.Stream
import cats.effect._
import com.artkostm.posters.worker.scraper.EventScraper

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      module <- WorkerModule.init[IO]
      x <- module.eventStore.findByDate(Instant.now())
      c = x
      _ <- test(module.scraper)
            .compile
            .drain
    } yield ExitCode.Success

  def test(scraper: EventScraper[IO]) = {

    val dayStream = Stream
      .range(-2, 31)
      .covary[IO]
      .map(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(_, ChronoUnit.DAYS))
      .mapAsyncUnordered(4)(scraper.event)

    val insertDays = dayStream.mapAsyncUnordered(4)(_ => IO { println("save day") })
    val saveEvents = dayStream
      .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
      .mapAsyncUnordered(4)(event => IO { println(s"save event: ${event.media.link}") })

    insertDays >> saveEvents
    import scala.concurrent.duration._
    //insertDays.concurrently(saveEvents)
//    Stream.sleep_
    val graph = Stream(insertDays, saveEvents).parJoin(2)
    graph.merge(Stream.awakeEvery[IO](24 hours) >> graph)
  }
}

object InstantTest extends App {
  val dates = (-2 to 30).map(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(_, ChronoUnit.DAYS))
  val format = DateTimeFormatter
    .ofPattern("yyyy/MM/dd")
    .withLocale(Locale.ENGLISH)
    .withZone(ZoneId.of("Europe/Minsk"))

  dates.map(format.format).foreach(println)
}
