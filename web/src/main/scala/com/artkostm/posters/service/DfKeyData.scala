package com.artkostm.posters.service

import java.time.LocalDate

import com.artkostm.posters.categories.Category
import com.artkostm.posters.interfaces.dialog.DateRange
import com.artkostm.posters.interfaces.dialog.v1.{DialogflowRequest => DFR1, Period => P1}
import com.artkostm.posters.interfaces.dialog.v2.{DialogflowRequest => DFR2, Period => P2}

/**
  * A type class to extract data from Dialogflow requests for both V1 and V2
  * @tparam DfRequest
  */
trait DfKeyData[DfRequest] {
  type Period <: DateRange
  def categories(request: DfRequest): List[String]
  def date(request: DfRequest): Option[LocalDate]
  def period(request: DfRequest): Option[Period]
  def hasAllCategory(request: DfRequest): Boolean
  def incomplete(request: DfRequest): Boolean
}

object DfKeyData {
  implicit class DfKeyDataOps[DfRequest: DfKeyData](request: DfRequest) {
    def categories: List[String]                    = implicitly[DfKeyData[DfRequest]].categories(request)
    def date: Option[LocalDate]                     = implicitly[DfKeyData[DfRequest]].date(request)
    def period: Option[DfKeyData[DfRequest]#Period] = implicitly[DfKeyData[DfRequest]].period(request)
    def hasAllCategory: Boolean                     = implicitly[DfKeyData[DfRequest]].hasAllCategory(request)
    def incomplete: Boolean                         = implicitly[DfKeyData[DfRequest]].incomplete(request)
  }

  implicit val df1KeyData: DfKeyData[DFR1] = new DfKeyData[DFR1] {
    override type Period = P1

    override def categories(request: DFR1): List[String] = request.result.parameters.category

    override def date(request: DFR1): Option[LocalDate] = request.result.parameters.datetime.date

    override def period(request: DFR1): Option[Period] = request.result.parameters.datetime.period

    override def hasAllCategory(request: DFR1): Boolean = categories(request).contains(Category.All.entryName)

    override def incomplete(request: DFR1): Boolean = request.result.actionIncomplete
  }

  implicit val df2KeyData: DfKeyData[DFR2] = new DfKeyData[DFR2] {
    override type Period = P2

    override def categories(request: DFR2): List[String] = request.queryResult.parameters.category

    override def date(request: DFR2): Option[LocalDate] = request.queryResult.parameters.datetime.date

    override def period(request: DFR2): Option[Period] = request.queryResult.parameters.datetime.period

    override def hasAllCategory(request: DFR2): Boolean = categories(request).contains(Category.All.entryName)

    override def incomplete(request: DFR2): Boolean = !request.queryResult.allRequiredParamsPresent
  }
}
