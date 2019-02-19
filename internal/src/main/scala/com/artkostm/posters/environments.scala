package com.artkostm.posters

object environments {
  import enumeratum._

  sealed abstract class AppEnvironment extends EnumEntry
  object AppEnvironment extends Enum[AppEnvironment] {
    case object Local      extends AppEnvironment
    case object Heroku     extends AppEnvironment
    case object Production extends AppEnvironment

    val values = findValues
  }
}

object categories {
  import enumeratum._

  sealed abstract class Category(override val entryName: String) extends EnumEntry

  object Category extends Enum[Category] {
    case object Movie extends Category("Кино")

    val values = findValues
  }

  // For validating path param in http4s routes
  object CategoryVar {
    def unapply(arg: String): Option[Category] = Category.withNameInsensitiveOption(arg)
  }
}
