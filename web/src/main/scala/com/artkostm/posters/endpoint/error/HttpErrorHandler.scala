package com.artkostm.posters.endpoint.error

import cats.Monad
import com.artkostm.posters.jsoniter._
import com.artkostm.posters.ValidationError
import com.artkostm.posters.ValidationError._
import com.artkostm.posters.endpoint.ApiError
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class HttpErrorHandler[F[_]: Monad] extends Http4sDsl[F] {

  // TODO: update api codes
  val handle: ValidationError => F[Response[F]] = {
    case CategoryNotFoundError(name, date)   => NotFound(ApiError(s"Cannot find '$name' category using date=$date", 404))
    case CategoriesNotFoundError(date)       => NotFound(ApiError(s"Cannot find categories using date=$date", 404))
    case EventInfoNotFoundError(link)        => NotFound(ApiError(s"Cannot find event using $link", 404))
    case RoleDoesNotExistError(role)         => BadRequest(ApiError(s"There is no $role.", 400))
    case IntentDoesNotExistError(name, date) => NotFound(ApiError(s"Can't find '$name' event on $date", 404))
    case LeaveEventError                     => Forbidden(ApiError("You cannot leave the event!", 403))
  }
}
