package com.artkostm.posters

import java.time.Instant

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.W
import cats.effect._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.config.WebConfiguration
import com.artkostm.posters.endpoint.InfoEndpoint
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
  def transactor[F[_]: Async: ContextShift](dbConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](driverClassName = dbConfig.driver.value,
                                                    url = dbConfig.url,
                                                    user = dbConfig.user,
                                                    pass = dbConfig.password,
                                                    connectEC = ce,
                                                    transactEC = te)
    } yield xa
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
      xa        <- transactor[IO](config.db)
      infoStore = new InfoStoreInterpreter(xa.trans)
      x <- BlazeServerBuilder[IO]
            .bindHttp(8080, "localhost")
            .withHttpApp(Router("/" -> InfoEndpoint(infoStore)).orNotFound)
            .resource
    } yield x)
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
