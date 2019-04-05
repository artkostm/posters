package com.artkostm.posters.interpreter

import cats.Functor
import cats.data.EitherT
import com.artkostm.posters.ValidationError.IntentDoesNotExistError
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

class VisitorValidationInterpreter[F[_]: Functor](store: VisitorStore[F]) extends VisitorValidationAlgebra[F] {
  override def exists(intent: Intent): EitherT[F, IntentDoesNotExistError, Intents] =
    EitherT.fromOptionF(
      store.find(intent.eventDate, intent.eventName),
      IntentDoesNotExistError(intent.eventName, intent.eventDate)
    )
}
