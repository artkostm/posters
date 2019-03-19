package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.auth.User
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class InfoEndpoint[F[_]: Effect](repository: InfoStore[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._
  import ValueCodecs._
  import com.artkostm.posters.ValidationError._

  private def getEventInfo(): AuthedService[User, F] = AuthedService {
    case GET -> Root / "events" :? LinkMatcher(link) as User(_, role) =>
      println(role)
      for {
        info <- EitherT.fromOptionF(repository.find(link), ApiError(s"Cannot find event using $link", 404)).value
        resp <- info.fold(NotFound(_), Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = getEventInfo()
}

object InfoEndpoint {
  def apply[F[_]: Effect](repository: InfoStore[F]): AuthedService[User, F] = new InfoEndpoint(repository).endpoints
}
