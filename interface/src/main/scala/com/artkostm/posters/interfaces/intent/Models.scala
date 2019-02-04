package com.artkostm.posters.interfaces.intent

import java.time.Instant

final case class Intent(date: Instant,
                        event_name: String,
                        vids: List[String] = List.empty,
                        uids: List[String] = List.empty)
