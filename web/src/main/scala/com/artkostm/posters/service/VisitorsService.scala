package com.artkostm.posters.service

import cats.Monad
import cats.data.EitherT
import com.artkostm.posters.ValidationError
import com.artkostm.posters.ValidationError.{LeaveEventError, RoleDoesNotExistError}
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.endpoint.auth.role.Role.{Volunteer, User => UserRole}
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

class VisitorsService[F[_]: Monad](repository: VisitorStore[F], validator: VisitorValidationAlgebra[F]) {
  def saveOrUpdateIntent(role: String, intent: Intent): EitherT[F, RoleDoesNotExistError, Intents] =
    on[RoleDoesNotExistError, Intents](role) {
      EitherT.liftF(repository.asPlainUser(intent))
    } {
      EitherT.liftF(repository.asVolunteer(intent))
    } {
      EitherT.leftT(RoleDoesNotExistError(role))
    }
//    Role.withNameInsensitiveOption(role) match {
//      case Some(User)      => EitherT.liftF(repository.asPlainUser(intent))
//      case Some(Volunteer) => EitherT.liftF(repository.asVolunteer(intent))
//      case _               => EitherT.leftT(RoleDoesNotExistError(role))
//    }

  def leaveEvent(intent: Intent, user: User): EitherT[F, ValidationError, Intents] =
    for {
      intents <- validator.exists(intent)
      role = user.role
      userId = "" // TODO: add user id
      _ <- on[ValidationError, Unit](role)(canLeave(userId, intents.uids))(canLeave(userId, intents.vids)) {
            EitherT.leftT(RoleDoesNotExistError(role))
          }
      updated <- EitherT.liftF(repository.leave(intent))
    } yield updated

  private def on[L, R](role: String)(onUserRole: => EitherT[F, L, R])(onVolunteerRole: => EitherT[F, L, R])(
      otherwise: => EitherT[F, L, R]): EitherT[F, L, R] =
    Role.withNameInsensitiveOption(role) match {
      case Some(UserRole)  => onUserRole
      case Some(Volunteer) => onVolunteerRole
      case _               => otherwise
    }

  private def canLeave(userId: String, ids: List[String]): EitherT[F, ValidationError, Unit] =
    if (ids.contains(userId)) EitherT.rightT(())
    else EitherT.leftT(LeaveEventError)
}
