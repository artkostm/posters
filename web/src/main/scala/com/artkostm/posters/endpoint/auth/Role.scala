package com.artkostm.posters.endpoint.auth

import cats.Applicative
import cats.data.EitherT
import cats.implicits._
import com.artkostm.posters.ValidationError
import com.artkostm.posters.ValidationError.ApiError

import scala.collection.immutable

object role {
  import enumeratum._

  sealed abstract class Role(override val entryName: String) extends EnumEntry

  object Role extends Enum[Role] {
    case object User      extends Role("User")
    case object Volunteer extends Role("Volunteer")

    override def values: immutable.IndexedSeq[Role] = findValues

    def exists(role: String): Boolean = Role.withNameInsensitiveOption(role).isDefined

    // TODO: change error type
    def on[F[_]: Applicative, T](role: String,
                                 userAction: => F[T],
                                 volunteerAction: => F[T]): EitherT[F, ApiError, T] =
      Role.withNameInsensitiveOption(role) match {
        case Some(User)      => EitherT.liftF(userAction())
        case Some(Volunteer) => EitherT.liftF(volunteerAction())
        case _               => EitherT.leftT(Applicative[F].pure(ApiError(s"There is no $role.", 400)))
      }
  }
}
