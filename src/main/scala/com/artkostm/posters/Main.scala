package com.artkostm.posters

import java.net.URI

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.W
import cats.effect._
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  import ciris.env

  import ciris.refined._

  println(env[ApiKey]("API_KEY").value match {
    case Right(key) => s"Here is the key: $key"
    case _ => "Something went wrong"
  })

  println(env[URI]("pg").value match {
    case Right(url) => s"Here is the url: $url"
    case _ => "Something went wrong"
  })

  val helloWorldService = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }

  import fs2.Stream

  import fs2.StreamApp.ExitCode

  import org.http4s.server.blaze._

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(helloWorldService, "/")
      .serve
//  val builder = BlazeBuilder[IO].bindHttp(8080, "localhost").mountService(helloWorldService, "/").start
//
//  val server = builder.unsafeRunSync()
//
//  Thread.sleep(100000)
//
//  server.shutdown.unsafeRunSync()
}//extends PostersServer
