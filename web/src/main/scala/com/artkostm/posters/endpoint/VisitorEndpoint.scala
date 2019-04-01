package com.artkostm.posters.endpoint

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.algebra.VisitorStore
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class VisitorEndpoint[F[_]: Effect](repository: VisitorStore[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._
  import com.artkostm.posters.jsoniter.codecs._
  import com.artkostm.posters.ValidationError._

  private def saveVisitors(): AuthedService[User, F] = AuthedService {
    case authed @ POST -> Root / "visitors" as User(_, role) =>
      for {
        intent  <- authed.req.as[Intent]
        created <- Role.on[F, Intents](role, repository.asPlainUser(intent), repository.asVolunteer(intent)).value
        resp    <- created.fold(BadRequest(_), Created(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = saveVisitors()
}

object VisitorEndpoint {
  def apply[F[_]: Effect](repository: VisitorStore[F]): AuthedService[User, F] =
    new VisitorEndpoint(repository).endpoints
}
