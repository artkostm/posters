package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.EventInfoNotFound
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.EventInfo
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class InfoEndpoint[F[_]: Effect](repository: InfoStore[F]) extends Http4sDsl[F] {
  import com.artkostm.posters.jsoniter._

  /* Parses out link query param */
  object LinkMatcher extends QueryParamDecoderMatcher[String]("link")

  implicit val eventInfoCodec: JsonValueCodec[EventInfo] = JsonCodecMaker.make[EventInfo](CodecMakerConfig())

  implicit val eventInfoNotFoundCodec: JsonValueCodec[EventInfoNotFound] =
    JsonCodecMaker.make[EventInfoNotFound](CodecMakerConfig())

  private def getEventInfo(): AuthedService[String, F] = AuthedService {
    case GET -> Root / "events" :? LinkMatcher(link) as _ =>
      for {
        info <- EitherT.fromOptionF(repository.find(link), EventInfoNotFound(s"Cannot find event using $link")).value
        resp <- info.fold(NotFound(_), Ok(_))
      } yield resp
  }

  def endpoints(): AuthedService[String, F] = getEventInfo()
}

object InfoEndpoint {
  def apply[F[_]: Effect](repository: InfoStore[F]): AuthedService[String, F] = new InfoEndpoint(repository).endpoints()
}
