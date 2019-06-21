package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Sync
import com.artkostm.posters.ValidationError.EventInfoNotFoundError
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

class InfoEndpoint[F[_]: Sync](repository: InfoStore[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def getEventInfo(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root / ApiVersion / "events" :? LinkMatcher(link) as _ =>
      for {
        info <- EitherT.fromOptionF(repository.find(link), EventInfoNotFoundError(link)).value
        resp <- info.fold(H.handle, Ok(_))
      } yield resp
  }

  override def endpoints: AuthedRoutes[User, F] = getEventInfo()
}

object InfoEndpoint {
  def apply[F[_]: Sync: HttpErrorHandler](repository: InfoStore[F]): InfoEndpoint[F] =
    new InfoEndpoint(repository)
}
