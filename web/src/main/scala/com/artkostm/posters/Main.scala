package com.artkostm.posters

import java.time.format.DateTimeFormatterBuilder
import java.time.{LocalDate, ZoneId}
import java.time.temporal.{ChronoField, ChronoUnit, TemporalAccessor}

import cats.effect._
import org.http4s.server.blaze._
import org.http4s.implicits._
import cats.implicits._
import com.artkostm.posters.interfaces.dialog.v1._
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.server.{Router, Server}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    server.use(_ => IO.never).as(ExitCode.Success)

  def server(): Resource[IO, Server[IO]] =
    for {
      module    <- WebModule.init
      endpoints <- Resource.liftF(module.endpoints.value)
      server <- BlazeServerBuilder[IO]
                 .withNio2(true)
                 .bindHttp(module.config.http.port.value, "0.0.0.0")
                 .withHttpApp(Router("/" -> endpoints.orNull).orNotFound)
                 .resource
    } yield server
}

object testMonocle extends App {
  val req = DialogflowRequest(
    "id",
    "lang",
    Result(
      "source",
      "resolvedQuery",
      None,
      "action",
      true,
      Parameters(List(), Datetime(None, Some(Period(LocalDate.now, LocalDate.now)))),
      List(),
      Metadata("", "", "", ""),
      Fulfillment("speech", List()),
      1.0
    ),
    Status(1, "errorType", Some(true)),
    "sessionId"
  )

  import com.github.plokhotnyuk.jsoniter_scala.core._

//  implicit val dd = endpoint.periodV1JsonValueCodec

  implicit val codec = JsonCodecMaker.make[DialogflowRequest](CodecMakerConfig())

//  println(new String(writeToArray(req)))
  println(readFromArray[DialogflowRequest](
    """{"id":"id","lang":"lang","result":{"source":"source","resolvedQuery":"resolvedQuery","action":"action","actionIncomplete":true,"parameters":{"datetime":{"period":"2019-04-15/2019-04-13"}},"metadata":{"intentId":"","webhookUsed":"","webhookForSlotFillingUsed":"","intentName":""},"fulfillment":{"speech":"speech"},"score":1.0},"status":{"code":1,"errorType":"errorType","webhookTimedOut":true},"sessionId":"sessionId"}""".getBytes))

//  val pr = monocle.Prism.partial[DialogflowRequest, List[String]] {
//    case DialogflowRequest(_, QueryResult(_, Parameters(category, _), _, _, _, _, _), _, _) => category
//  }
}

object dateTest extends App {
  val FMT = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd/yyyy-MM-dd")
    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("Europe/Minsk"))
  println(FMT.parse("2019/02/17", (tmp: TemporalAccessor) => LocalDate.from(tmp)))
}
