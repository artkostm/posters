package com.artkostm.posters.service

import java.time.LocalDate

import cats.Monad
import cats.data.EitherT
import com.artkostm.posters.ValidationError
import com.artkostm.posters.ValidationError.{IntentDoesNotExistError, LeaveEventError, RoleDoesNotExistError}
import com.artkostm.posters.algebra.{VisitorStore, VisitorValidationAlgebra}
import com.artkostm.posters.endpoint.auth.role.Role
import com.artkostm.posters.endpoint.auth.role.Role.{Volunteer, User => UserRole}
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.intent.{Intent, Intents}

class VisitorsService[F[_]: Monad](repository: VisitorStore[F], validator: VisitorValidationAlgebra[F]) {

  def findIntent(eventName: String, date: LocalDate): EitherT[F, IntentDoesNotExistError, Intents] =
    EitherT.fromOptionF(repository.find(date, eventName), IntentDoesNotExistError(eventName, date))

  def saveOrUpdateIntent(role: String, intent: Intent): EitherT[F, RoleDoesNotExistError, Intents] =
    on[RoleDoesNotExistError, Intents](role) {
      EitherT.liftF(repository.asPlainUser(intent))
    } {
      EitherT.liftF(repository.asVolunteer(intent))
    } {
      EitherT.leftT(RoleDoesNotExistError(role))
    }

  def leaveEvent(intent: Intent, user: User): EitherT[F, ValidationError, Intents] =
    for {
      // TODO: try to do this in the same transaction
      intents <- validator.exists(intent)
      role    = user.role
      userId  = user.id
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
    ids.contains(userId) match {
      case true  => EitherT.rightT(())
      case false => EitherT.leftT(LeaveEventError)
    }
}
