package com.artkostm.posters.interfaces

package object auth {
  final case class User(apiToken: String, role: String, id: String)
}
