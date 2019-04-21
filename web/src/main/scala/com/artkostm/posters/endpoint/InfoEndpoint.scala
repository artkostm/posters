package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.ValidationError.EventInfoNotFoundError
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class InfoEndpoint[F[_]: Effect](repository: InfoStore[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def getEventInfo(): AuthedService[User, F] = AuthedService {
    case GET -> Root / ApiVersion / "events" :? LinkMatcher(link) as _ =>
      for {
        info <- EitherT.fromOptionF(repository.find(link), EventInfoNotFoundError(link)).value
        resp <- info.fold(H.handle, Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = getEventInfo()
}

object InfoEndpoint {
  def apply[F[_]: Effect: HttpErrorHandler](repository: InfoStore[F]): InfoEndpoint[F] =
    new InfoEndpoint(repository)
}
