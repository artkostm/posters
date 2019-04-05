package com.artkostm.posters

import java.time.LocalDate

sealed trait ValidationError extends Throwable

object ValidationError {
  final case class CategoryNotFoundError(name: String, date: LocalDate) extends ValidationError
  final case class CategoriesNotFoundError(date: LocalDate) extends ValidationError
  final case class EventInfoNotFoundError(link: String) extends ValidationError
  final case class RoleDoesNotExistError(role: String) extends ValidationError
  final case class IntentDoesNotExistError(eventName: String, date: LocalDate) extends ValidationError
}
