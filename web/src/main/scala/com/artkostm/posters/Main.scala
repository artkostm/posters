package com.artkostm.posters

import java.time.format.DateTimeFormatterBuilder
import java.time.{Instant, LocalDate, ZoneId}
import java.time.temporal.{ChronoField, ChronoUnit, TemporalAccessor}

import cats.effect._
import com.artkostm.posters.endpoint.auth.JwtTokenAuthMiddleware
import com.artkostm.posters.interfaces.dialog.v2._
import org.http4s.server.Router

object Main extends IOApp {
  import org.http4s.server.blaze._
  import org.http4s.implicits._
  import cats.implicits._
  override def run(args: List[String]): IO[ExitCode] =
    (for {
      module <- WebModule.init
      auth   <- Resource.liftF(JwtTokenAuthMiddleware[IO](module.config.api))
      exit <- BlazeServerBuilder[IO]
               .withNio2(true)
               .bindHttp(8080, "localhost")
               .withHttpApp(Router("/" -> auth(module.endpoints)).orNotFound)
               .resource
    } yield exit)
      .use(_ => IO.never)
      .as(ExitCode.Success)
}

object testMonocle extends App {
  val req = DialogflowRequest(
    "respId",
    QueryResult(
      "query text",
      Parameters(List("кино", "цирк"), Datetime(Some(Instant.now()), None)),
      true,
      Intent("name", "display name"),
      1.0,
      DiagnosticInfo(),
      "language code"
    ),
    OriginalDetectIntentRequest(Payload()),
    "session"
  )

  List() match {
    case l @ _ :: _ => println(s"not empty: $l")
    case _          => println("empty")
  }

  println(Instant.now().truncatedTo(ChronoUnit.DAYS))

//  val pr = monocle.Prism.partial[DialogflowRequest, List[String]] {
//    case DialogflowRequest(_, QueryResult(_, Parameters(category, _), _, _, _, _, _), _, _) => category
//  }
}


object dateTest extends App {
  val FMT = new DateTimeFormatterBuilder()
    .appendPattern("yyyy/MM/dd")
    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("Europe/Minsk"))
  println(FMT.parse("2019/02/17", (tmp: TemporalAccessor) => Instant.from(tmp)))
}