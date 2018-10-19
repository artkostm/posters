package com.artkostm.posters

import java.time.Instant

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.W
import cats.effect._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  case class Personalities(info: String)
  case class User(name: String, personalities: List[Personalities], birth: Instant)

  object User {
    implicit val codec: JsonValueCodec[User] = JsonCodecMaker.make[User](CodecMakerConfig())
  }

  val helloWorldService = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(User(name, List(Personalities(name.toUpperCase)), Instant.now()))
  }

  import fs2.Stream
  import fs2.StreamApp.ExitCode
  import org.http4s.server.blaze._

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(helloWorldService, "/")
      .serve
}//extends PostersServer
