package com.artkostm.posters.interfaces

package object auth {
  final case class User(apiKey: String, role: String, id: String)
}
