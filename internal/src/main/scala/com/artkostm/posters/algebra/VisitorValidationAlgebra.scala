package com.artkostm.posters.algebra

import cats.data.EitherT
import com.artkostm.posters.ValidationError.IntentDoesNotExistError
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

trait VisitorValidationAlgebra[F[_]] {
  def exists(intent: Intent): EitherT[F, IntentDoesNotExistError, Intents]
}
