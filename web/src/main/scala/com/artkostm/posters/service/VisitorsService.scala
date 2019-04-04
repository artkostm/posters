package com.artkostm.posters.service

import cats.{Applicative, Monad}
import cats.data.EitherT
import com.artkostm.posters.ValidationError.ApiError
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.endpoint.auth.role.Role.{User, Volunteer}
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

class VisitorsService[F[_]: Monad](repository: VisitorStore[F], validator: VisitorValidationAlgebra[F]) {
  // TODO: change error type
  def saveOrUpdateIntent(role: String, intent: Intent): EitherT[F, ApiError, Intents] =
    Role.withNameInsensitiveOption(role) match {
      case Some(User)      => EitherT.liftF(repository.asPlainUser(intent))
      case Some(Volunteer) => EitherT.liftF(repository.asVolunteer(intent))
      case _               => EitherT.leftT(Applicative[F].pure(ApiError(s"There is no $role.", 400)))
    }

  def leaveEvent(intent: Intent): EitherT[F, ApiError, Intents] =
    for {
      _       <- validator.exists(intent)
      updated <- EitherT.liftF(repository.leave(intent))
    } yield updated
}
