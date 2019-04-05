package com.artkostm.posters.endpoint.error

import cats.Monad
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.ValidationError
import com.artkostm.posters.ValidationError._
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class HttpErrorHandler[F[_]: Monad] extends Http4sDsl[F] {

  val handler: ValidationError => F[Response[F]] = {
    case CategoryNotFoundError(name, date)   => NotFound(ApiError("", 3))
    case CategoriesNotFoundError(date)       => NotFound("")
    case EventInfoNotFoundError(link)        => NotFound("")
    case RoleDoesNotExistError(role)         => BadRequest(ApiError(s"There is no $role.", 400))
    case IntentDoesNotExistError(name, date) => NotFound(ApiError(s"Can't find '$name' event on $date", 404))
  }
}

