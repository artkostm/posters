package com.artkostm.posters.dialog

import org.joda.time.DateTime

case class FlowKeyData(category: List[String], date: Option[DateTime], period: Option[String])

trait FlowKeyDataExtractor {
  def actionIncomplete(req: DialogflowRequest): Boolean = req.result.actionIncomplete

  def extract(request: DialogflowRequest): FlowKeyData = {
    val category = request.result.parameters.category
    val date = request.result.parameters.datetime.date
    val period = request.result.parameters.datetime.period
    FlowKeyData(category, date, period)
  }
}

object FlowKeyDataExtractor extends FlowKeyDataExtractor {
  private val allCategories = "Все"

  def shouldShowAll(request: DialogflowRequest): Boolean = {
    val category = request.result.parameters.category
    category.size == 1 && category.contains(allCategories)
  }
}
