package com.artkostm.posters

object environments {
  import enumeratum._

  sealed abstract class AppEnvironment extends EnumEntry
  object AppEnvironment extends Enum[AppEnvironment] {
    case object Local extends AppEnvironment
    case object Heroku extends AppEnvironment
    case object Production extends AppEnvironment

    val values = findValues
  }
}
