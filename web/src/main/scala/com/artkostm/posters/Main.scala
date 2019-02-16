package com.artkostm.posters

import java.time.Instant

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.W
import cats.effect._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.config.WebConfiguration
import com.artkostm.posters.endpoint.InfoEndpoint
import com.artkostm.posters.endpoint.auth.JwtTokenAuthMiddleware
import com.artkostm.posters.interfaces.dialog.v2._
import com.artkostm.posters.interpreter.InfoStoreInterpreter
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import org.http4s.implicits._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.Router

import scala.concurrent.Future

object Main extends IOApp {
//  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]
//
//  case class Personalities(info: String)
//  case class User(name: String, personalities: List[Personalities], birth: Instant)
//
//  object User {
//    implicit val codec: JsonValueCodec[User] = JsonCodecMaker.make[User](CodecMakerConfig())
//  }
//
//  import com.artkostm.posters.jsoniter._
//
//  val helloWorldService = HttpService[IO] {
//    case req @ POST -> Root / "hello" =>
//      for {
//        user <- req.as[User]
//        resp <- Ok(User(user.name.toUpperCase, personalities = List.empty, Instant.now().minusSeconds(5000)))
//      } yield resp
//    case GET -> Root / "hello" / name =>
//      Ok(User(name, List(Personalities(name.toUpperCase)), Instant.now()))
//    case GET -> Root / "test1" =>
//      Ok("hello")
//  }

  import org.http4s.server.blaze._
  import cats.implicits._
  override def run(args: List[String]): IO[ExitCode] =
    (for {
      config    <- Resource.liftF(WebConfiguration.load[IO])
      xa        <- DatabaseConfig.transactor[IO](config.db)
      infoStore = new InfoStoreInterpreter(xa.trans)
      auth      <- Resource.liftF(JwtTokenAuthMiddleware[IO](Some("12345")))
      exit <- BlazeServerBuilder[IO]
               .bindHttp(8080, "localhost")
               .withHttpApp(Router("/" -> auth(InfoEndpoint(infoStore))).orNotFound)
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

//  val pr = monocle.Prism.partial[DialogflowRequest, List[String]] {
//    case DialogflowRequest(_, QueryResult(_, Parameters(category, _), _, _, _, _, _), _, _) => category
//  }
}
