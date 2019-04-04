package com.artkostm.posters.algebra

import cats.data.EitherT
import com.artkostm.posters.ValidationError.ApiError
import com.artkostm.posters.interfaces.intent.Intent

trait VisitorValidationAlgebra[F[_]] {
  def exists(intent: Intent): EitherT[F, ApiError, Unit]
}
