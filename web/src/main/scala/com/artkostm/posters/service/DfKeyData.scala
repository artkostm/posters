package com.artkostm.posters.service

import java.time.LocalDate

import com.artkostm.posters.categories.Category
import com.artkostm.posters.interfaces.dialog.v1.{DialogflowRequest => DFR1, Period => P1}
import com.artkostm.posters.interfaces.dialog.v2.{DialogflowRequest => DFR2, Period => P2}

trait DfKeyData[DfRequest] {
  type Period
  def categories(request: DfRequest): List[String]
  def date(request: DfRequest): Option[LocalDate]
  def period(request: DfRequest): Option[Period]
  def hasAllCategory(request: DfRequest): Boolean
}

object DfKeyData {
  implicit class DfKeyDataOps[DfRequest: DfKeyData](response: DfRequest) {
    def categories: List[String]                    = implicitly[DfKeyData[DfRequest]].categories(response)
    def date: Option[LocalDate]                     = implicitly[DfKeyData[DfRequest]].date(response)
    def period: Option[DfKeyData[DfRequest]#Period] = implicitly[DfKeyData[DfRequest]].period(response)
    def hasAllCategory: Boolean                     = implicitly[DfKeyData[DfRequest]].hasAllCategory(response)
  }

  implicit val df1KeyData: DfKeyData[DFR1] = new DfKeyData[DFR1] {
    override type Period = P1

    override def categories(request: DFR1): List[String] = request.result.parameters.category

    override def date(request: DFR1): Option[LocalDate] = request.result.parameters.datetime.date

    override def period(request: DFR1): Option[Period] = request.result.parameters.datetime.period

    override def hasAllCategory(request: DFR1): Boolean = categories(request).contains(Category.All.entryName)
  }

  implicit val df2KeyData: DfKeyData[DFR2] = new DfKeyData[DFR2] {
    override type Period = P2

    override def categories(request: DFR2): List[String] = request.queryResult.parameters.category

    override def date(request: DFR2): Option[LocalDate] = request.queryResult.parameters.datetime.date

    override def period(request: DFR2): Option[Period] = request.queryResult.parameters.datetime.period

    override def hasAllCategory(request: DFR2): Boolean = categories(request).contains(Category.All.entryName)
  }
}
