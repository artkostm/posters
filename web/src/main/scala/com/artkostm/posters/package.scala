package com.artkostm

import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

package object posters {
  val FMT = new DateTimeFormatterBuilder()
    .appendPattern("yyyy/MM/dd")
    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("Europe/Minsk"))
}
