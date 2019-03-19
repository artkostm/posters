package com.artkostm.posters.interfaces.intent

import java.time.Instant

final case class Intents(date: Instant,
                         eventName: String,
                         vids: List[String] = List.empty,
                         uids: List[String] = List.empty)

final case class Intent(date: Instant, eventName: String, userId: String)
