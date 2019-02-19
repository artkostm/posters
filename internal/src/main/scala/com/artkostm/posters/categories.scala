package com.artkostm.posters

object categories {
  import enumeratum._

  sealed abstract class Category(override val entryName: String) extends EnumEntry

  object Category extends Enum[Category] {
    case object Movie      extends Category("Кино")
    case object Concert    extends Category("Концерты")
    case object Party      extends Category("Вечеринки")
    case object Spectacle  extends Category("Спектакли")
    case object Exposition extends Category("Выставки")
    case object Circus     extends Category("Цирк")
    case object Free       extends Category("Бесплатные")
    case object All        extends Category("Все")

    val values = findValues
  }

  // For validating the name path param in http4s routes
  object CategoryVar {
    def unapply(arg: String): Option[Category] = Category.withNameInsensitiveOption(arg)
  }
}
