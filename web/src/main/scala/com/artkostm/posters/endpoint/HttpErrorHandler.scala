package com.artkostm.posters.endpoint

import cats.Monad
import com.artkostm.posters.ValidationError
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class HttpErrorHandler[F[_]: Monad] extends Http4sDsl[F] {
  import com.artkostm.posters.jsoniter._
  import ValidationError._

  val handle: ValidationError => F[Response[F]] = {
    case error: EventInfoNotFound => NotFound(error)
    case error: CategoryNotFound  => NotFound(error)
  }
}
