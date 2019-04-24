package com.artkostm.posters

import cats.effect._
import org.http4s.server.blaze._
import org.http4s.implicits._
import cats.implicits._
import com.typesafe.config.Config
import kamon.{Kamon, MetricReporter}
import kamon.http4s.middleware.server.KamonSupport
import kamon.metric.PeriodSnapshot
import org.http4s.server.{Router, Server}

object Main extends IOApp {
  Kamon.addReporter(new MetricReporter {
    override def reportPeriodSnapshot(snapshot: PeriodSnapshot): Unit = println(s"Report: $snapshot")

    override def start(): Unit = println("Start gathering metrics")

    override def stop(): Unit = println("Stop gathering metrics")

    override def reconfigure(config: Config): Unit = println(s"Reconfiguration using $config")
  })

  override def run(args: List[String]): IO[ExitCode] =
    server.use(_ => IO.never).as(ExitCode.Success)

  def server(): Resource[IO, Server[IO]] =
    for {
      module    <- WebModule.init
      endpoints <- Resource.liftF(module.endpoints.value)
      server <- BlazeServerBuilder[IO]
                 .withNio2(true)
                 .bindHttp(module.config.http.port.value, "0.0.0.0")
                 .withHttpApp(KamonSupport(Router("/" -> endpoints.orNull)).orNotFound)
                 .resource
    } yield server
}
