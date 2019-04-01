package com.artkostm.posters.endpoint.auth

import scala.collection.immutable

object role {
  import enumeratum._

  sealed abstract class Role(override val entryName: String) extends EnumEntry

  object Role extends Enum[Role] {
    case object User      extends Role("User")
    case object Volunteer extends Role("Volunteer")

    override def values: immutable.IndexedSeq[Role] = findValues

    def exists(role: String): Boolean = Role.withNameInsensitiveOption(role).isDefined
  }
}
