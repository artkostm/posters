package com.artkostm.posters.endpoint

import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.EventInfo
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class InfoEndpoint[F[_]: Effect](repository: InfoStore[F]) extends Http4sDsl[F] {
  import com.artkostm.posters.jsoniter._

  /* Parses out link query param */
  object LinkMatcher extends QueryParamDecoderMatcher[String]("link")

  implicit val codec: JsonValueCodec[EventInfo] = JsonCodecMaker.make[EventInfo](CodecMakerConfig())

  private def getEventInfo(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "events" :? LinkMatcher(link) =>
      for {
        info <- repository.find(link)
        resp <- info.map(Ok(_)).getOrElse(NotFound("The info was not found!"))
      } yield resp
  }

  def endpoints(): HttpRoutes[F] = getEventInfo()
}

object InfoEndpoint {
  def apply[F[_]: Effect](repository: InfoStore[F]): HttpRoutes[F] = new InfoEndpoint(repository).endpoints()
}
