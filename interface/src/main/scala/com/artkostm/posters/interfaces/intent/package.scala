package com.artkostm.posters.interfaces

import java.time.LocalDate

package object intent {
  final case class Intents(eventDate: LocalDate,
                           eventName: String,
                           vids: List[String] = List.empty,
                           uids: List[String] = List.empty)

  final case class Intent(eventDate: LocalDate, eventName: String, userId: String)
}
