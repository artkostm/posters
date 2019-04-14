package com.artkostm.posters.endpoint

import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.Intent
import com.artkostm.posters.service.VisitorsService
import org.http4s.AuthedService
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._

class VisitorEndpoint[F[_]: Effect](service: VisitorsService[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def saveVisitors(): AuthedService[User, F] = AuthedService {
    case authed @ POST -> Root / ApiVersion / "visitors" as User(_, role, _) =>
      for {
        intent  <- authed.req.as[Intent]
        created <- service.saveOrUpdateIntent(role, intent).value
        resp    <- created.fold(H.handle, Ok(_))
      } yield resp
  }

  private def leaveEvent(): AuthedService[User, F] = AuthedService {
    case authed @ DELETE -> Root / ApiVersion / "visitors" as user =>
      for {
        intent  <- authed.req.as[Intent]
        updated <- service.leaveEvent(intent, user).value
        resp    <- updated.fold(H.handle, Ok(_))
      } yield resp
  }

  private def findEvent(): AuthedService[User, F] = AuthedService {
    case GET -> Root / ApiVersion / "visitors" :? EventNameMatcher(eventName) +& DateMatcher(date) as _ =>
      for {
        intents <- service.findIntent(eventName, date).value
        resp    <- intents.fold(H.handle, Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = saveVisitors() <+> leaveEvent() <+> findEvent()
}

object VisitorEndpoint {
  def apply[F[_]: Effect: HttpErrorHandler](service: VisitorsService[F]): AuthedService[User, F] =
    new VisitorEndpoint(service).endpoints
}
