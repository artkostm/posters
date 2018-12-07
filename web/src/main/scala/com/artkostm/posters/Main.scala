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
import scala.concurrent.Future

object Main extends StreamApp[IO] {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  case class Personalities(info: String)
  case class User(name: String, personalities: List[Personalities], birth: Instant)

  object User {
    implicit val codec: JsonValueCodec[User] = JsonCodecMaker.make[User](CodecMakerConfig())
  }

  import com.artkostm.posters.jsoniter._

  val helloWorldService = HttpService[IO] {
    case req @ POST -> Root / "hello" =>
      for {
        user <- req.as[User]
        resp <- Ok(User(user.name.toUpperCase, personalities = List.empty, Instant.now().minusSeconds(5000)))
      } yield resp
    case GET -> Root / "hello" / name =>
      Ok(User(name, List(Personalities(name.toUpperCase)), Instant.now()))
    case GET -> Root / "test1" =>
      Ok("hello")
    case GET -> Root / "test2" =>
      Ok(Future.successful(User("Name", List(Personalities("Name".toUpperCase)), Instant.now())))
  }

  import fs2.Stream
  import fs2.StreamApp.ExitCode
  import org.http4s.server.blaze._

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(helloWorldService, "/")
      .serve
} //extends PostersServer

//object jjjjjj extends App {
//  //implicit val codec: JsonValueCodec[List[String]] = JsonCodecMaker.make[List[String]](CodecMakerConfig())
//  //val bytes = writeToArray(List("first", "value1", "second" ,"value2"))
//  //println(new String(bytes))
//
//  case class User(name: String, age: Int)
//  object User {
//    implicit val codec: JsonValueCodec[User] = JsonCodecMaker.make[User](CodecMakerConfig())
//    implicit val mapCodec: JsonValueCodec[Map[String, User]] = JsonCodecMaker.make[Map[String, User]](CodecMakerConfig())
//  }
//
//  val m: Map[String, User] = readFromArray(
//    """
//      |{"first":{"name":"Lol","age":12},"second":{"name":"Mongol","age":15}}
//    """.stripMargin.getBytes())(User.mapCodec)
//
//  println(m)
//}
