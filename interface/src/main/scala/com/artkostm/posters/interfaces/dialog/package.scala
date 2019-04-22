package com.artkostm.posters.interfaces

import java.time.LocalDate

import com.artkostm.posters.interfaces.schedule.Category

package object dialog {

  final case class ResponsePayload(categories: Seq[Category])

  trait DateRange {
    def startDate: LocalDate
    def endDate: LocalDate
  }
}
