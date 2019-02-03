package com.artkostm.posters.interfaces.intent

import java.time.Instant

final case class Intent(date: Instant,
                        eventName: String,
                        vids: List[String] = List.empty,
                        uids: List[String] = List.empty)
