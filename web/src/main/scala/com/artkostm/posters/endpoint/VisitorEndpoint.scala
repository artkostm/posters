package com.artkostm.posters.endpoint

import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.Intent
import com.artkostm.posters.service.VisitorsService
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl

class VisitorEndpoint[F[_]: Effect](service: VisitorsService[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._
  import com.artkostm.posters.jsoniter.codecs._
  import com.artkostm.posters.ValidationError._

  private def saveVisitors(): AuthedService[User, F] = AuthedService {
    case authed @ POST -> Root / "visitors" as User(_, role) =>
      for {
        intent  <- authed.req.as[Intent]
        created <- service.saveOrUpdateIntent(role, intent).value
        resp    <- created.fold(BadRequest(_), Created(_))
      } yield resp
  }

  private def leaveEvent(): AuthedService[User, F] = AuthedService {
    case authed @ DELETE -> Root / "visitors" as _ =>
      for {
        intent  <- authed.req.as[Intent]
        updated <- service.leaveEvent(intent).value
        // TODO: update error handling with correct http status codes
        resp    <- updated.fold(BadRequest(_), Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = saveVisitors() <+> leaveEvent()
}

object VisitorEndpoint {
  def apply[F[_]: Effect](service: VisitorsService[F]): AuthedService[User, F] =
    new VisitorEndpoint(service).endpoints
}
