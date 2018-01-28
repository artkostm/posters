package com.artkostm.posters.dialog

import org.joda.time.{DateTime, Days}

case class FlowKeyData(category: List[String], date: Option[DateTime], period: Option[String])

trait FlowKeyDataExtractor {
  def actionIncomplete(req: DialogflowRequest): Boolean = req.result.actionIncomplete

  def extract(request: DialogflowRequest): FlowKeyData = {
    val category = request.result.parameters.category
    val date = request.result.parameters.datetime.date
    val period = request.result.parameters.datetime.period
    FlowKeyData(category, date, period)
  }

  // period in the format yyyy-mm-dd/yyyy-mm-dd
  def getPeriod(period: String): List[DateTime] = {
    period.split("/") match {
      case Array(start, end) =>
        val startDate = DateTime.parse(start)
        val endDate = DateTime.parse(end)
        (0 to Days.daysBetween(startDate, endDate).getDays).map(startDate.plusDays(_)).toList
      case _ => throw new IllegalArgumentException ("Invalid period format")
    }
  }
}

object FlowKeyDataExtractor extends FlowKeyDataExtractor {
  private val allCategories = "Все"

  def shouldShowAll(request: DialogflowRequest): Boolean = {
    val category = request.result.parameters.category
    category.size == 1 && category.contains(allCategories)
  }
}
