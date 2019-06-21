package com.artkostm.posters.endpoint

import cats.implicits._
import cats.effect.Sync
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.jsoniter.codecs._
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.Intent
import com.artkostm.posters.service.VisitorsService
import org.http4s.AuthedRoutes
import org.http4s.dsl.Http4sDsl

class VisitorEndpoint[F[_]: Sync](service: VisitorsService[F])(implicit H: HttpErrorHandler[F])
    extends Http4sDsl[F]
    with EndpointsAware[F] {

  private def saveVisitors(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case authed @ POST -> Root / ApiVersion / "visitors" as User(_, role, _) =>
      for {
        intent  <- authed.req.as[Intent]
        created <- service.saveOrUpdateIntent(role, intent).value
        resp    <- created.fold(H.handle, Ok(_))
      } yield resp
  }

  private def leaveEvent(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case authed @ DELETE -> Root / ApiVersion / "visitors" as user =>
      for {
        intent  <- authed.req.as[Intent]
        updated <- service.leaveEvent(intent, user).value
        resp    <- updated.fold(H.handle, Ok(_))
      } yield resp
  }

  private def findEvent(): AuthedRoutes[User, F] = AuthedRoutes.of {
    case GET -> Root / ApiVersion / "visitors" :? EventNameMatcher(eventName) +& DateMatcher(date) as _ =>
      for {
        intents <- service.findIntent(eventName, date).value
        resp    <- intents.fold(H.handle, Ok(_))
      } yield resp
  }

  override def endpoints: AuthedRoutes[User, F] = findEvent()

  def throttled: AuthedRoutes[User, F] = saveVisitors() <+> leaveEvent()
}

object VisitorEndpoint {
  def apply[F[_]: Sync: HttpErrorHandler](service: VisitorsService[F]): VisitorEndpoint[F] =
    new VisitorEndpoint(service)
}
