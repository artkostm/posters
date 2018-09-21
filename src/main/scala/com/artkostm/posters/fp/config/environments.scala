package com.artkostm.posters.fp.config

object environments {
  import enumeratum._

  sealed abstract class AppEnvironment extends EnumEntry
  object AppEnvironment extends Enum[AppEnvironment] {
    case object Local extends AppEnvironment
    case object Production extends AppEnvironment

    val values = findValues
  }
}
