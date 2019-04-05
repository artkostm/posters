package com.artkostm.posters.service

import cats.{Applicative, Monad}
import cats.data.EitherT
import com.artkostm.posters.ValidationError.{IntentDoesNotExistError, RoleDoesNotExistError}
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.endpoint.auth.role.Role.{User, Volunteer}
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

class VisitorsService[F[_]: Monad](repository: VisitorStore[F], validator: VisitorValidationAlgebra[F]) {
  def saveOrUpdateIntent(role: String, intent: Intent): EitherT[F, RoleDoesNotExistError, Intents] =
    Role.withNameInsensitiveOption(role) match {
      case Some(User)      => EitherT.liftF(repository.asPlainUser(intent))
      case Some(Volunteer) => EitherT.liftF(repository.asVolunteer(intent))
      case _               => EitherT.leftT(Applicative[F].pure(RoleDoesNotExistError(role)))
    }

  def leaveEvent(intent: Intent): EitherT[F, IntentDoesNotExistError, Intents] =
    for {
      _       <- validator.exists(intent)
      updated <- EitherT.liftF(repository.leave(intent))
    } yield updated
}
