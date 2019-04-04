package com.artkostm.posters.interpreter

import cats.Functor
import cats.data.EitherT
import com.artkostm.posters.ValidationError.ApiError
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.interfaces.intent.Intent

class VisitorValidationInterpreter[F[_]: Functor](store: VisitorStore[F]) extends VisitorValidationAlgebra[F] {
  override def exists(intent: Intent): EitherT[F, ApiError, Unit] =
    EitherT.fromOptionF(
      store.find(intent.eventDate, intent.eventName),
      // TODO: change error type
      ApiError(s"Can't find '${intent.eventName}' event on ${intent.eventDate}", 404)
    )
}
