package com.artkostm.posters.interfaces.auth

final case class User(apiKey: String, role: String)

object Roles {
  val volunteer = "Volunteer"
  val user      = "User"

  private val roles = List(volunteer, user)

  val hasRole: String => Boolean = roles.contains
}
