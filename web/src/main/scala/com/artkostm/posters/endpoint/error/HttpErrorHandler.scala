package com.artkostm.posters.endpoint.error

import cats.data.{Kleisli, OptionT}
import cats.{ApplicativeError, MonadError}
import cats.implicits._
import com.artkostm.posters.ValidationError
import org.http4s._
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl

trait HttpErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

object RoutesHttpErrorHandler {
  def apply[F[_]: ApplicativeError[?[_], E], E <: Throwable](
      routes: HttpRoutes[F]
  )(handler: E => F[Response[F]]): HttpRoutes[F] =
    Kleisli { req =>
      OptionT(routes.run(req).value.handleErrorWith(e => handler(e).map(Option(_))))
    }
}

object HttpErrorHandler {
  def apply[F[_], E <: Throwable](implicit ev: HttpErrorHandler[F, E]) = ev
}

class ApiHttpErrorHandler[F[_]: MonadError[?[_], ValidationError]]
    extends HttpErrorHandler[F, ValidationError]
    with Http4sDsl[F] {
  import com.artkostm.posters.jsoniter._
  import ValidationError._

  private val handler: ValidationError => F[Response[F]] = {
    case error: ApiError => NotFound(error)
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}
