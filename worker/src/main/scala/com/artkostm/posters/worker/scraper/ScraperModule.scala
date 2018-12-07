package com.artkostm.posters.worker.scraper

import bloomfilter.CanGenerateHashFrom
import bloomfilter.CanGenerateHashFrom.CanGenerateHashFromString
import bloomfilter.mutable.BloomFilter
import cats.effect.{Async, IO}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import org.joda.time.DateTime

class ScraperModule[F[_]: Async](appConfig: AppConfig) {
  val eventScraper = new EventScraper[F](appConfig.scraper)
}

object testS extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    val stream = for {
      config  <- fs2.Stream.eval(AppConfiguration.load)
      scraper <- fs2.Stream.eval(IO.pure(new EventScraper[IO](config.scraper)))
      day     <- fs2.Stream.range(1, 31).covary[IO]
      event   <- fs2.Stream.eval(scraper.event(DateTime.now().plusDays(day)))
    } yield {
      println(event)
    }

    fs2.Stream.eval(stream.compile.drain.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitCode.fromInt(_)))
  }
}

object ttttttt extends App {
  case class Request(date: DateTime, name: String)
  val expectedElements  = 6
  val falsePositiveRate = 0.1

  implicit val requestHashGenerator = new CanGenerateHashFrom[Request] {
    override def generateHash(from: Request): Long =
      CanGenerateHashFromString.generateHash(s"${from.date.toLocalDate}-${from.name}")
  }

  val bf = BloomFilter[Request](expectedElements, falsePositiveRate)

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      println("CLOSING THE PROGRAM")
      bf.dispose()
    }
  })
  // Put an element
  bf.add(Request(DateTime.now, "1"))
  bf.add(Request(DateTime.now, "3"))
  bf.add(Request(DateTime.now, "5"))
  bf.add(Request(DateTime.now, "7"))
  bf.add(Request(DateTime.now, "10"))

  List(1, 4, 6, 12, 43).foreach(d => println(bf.mightContain(Request(DateTime.now, d.toString))))

  bf.writeTo(System.out)
  Thread.sleep(50000)
}
