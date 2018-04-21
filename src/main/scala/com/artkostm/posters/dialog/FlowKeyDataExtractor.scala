package com.artkostm.posters.dialog

import java.net.{URI, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets

import com.artkostm.posters.dialog.v1.{DialogflowRequest => DFRequestV1}
import com.artkostm.posters.dialog.v2.{Period, DialogflowRequest => DFRequestV2}
import org.joda.time.{DateTime, Days}

case class FlowKeyData(category: List[String], date: Option[DateTime], period: Option[String], range: Option[Period])

trait FlowKeyDataExtractor {
  def actionIncomplete(req: DFRequestV1): Boolean = req.result.actionIncomplete
  def actionIncomplete(req: DFRequestV2): Boolean = !req.queryResult.allRequiredParamsPresent

  def extract(request: DFRequestV1): FlowKeyData = {
    val category = request.result.parameters.category.map(URLDecoder.decode(_, StandardCharsets.UTF_8.name()))
    val date = request.result.parameters.datetime.date
    val period = request.result.parameters.datetime.period
    FlowKeyData(category, date, period, None)
  }

  def extract(request: DFRequestV2): FlowKeyData = {
    val category = request.queryResult.parameters.category
    val date = request.queryResult.parameters.datetime.date
    val period = request.queryResult.parameters.datetime.period
    FlowKeyData(category, date, None, period)
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

  def getPeriod(period: Period): List[DateTime] =
    (0 to Days.daysBetween(period.startDate, period.endDate).getDays).map(period.startDate.plusDays(_)).toList
}

object FlowKeyDataExtractor extends FlowKeyDataExtractor {
  private val allCategories = "Все"

  def shouldShowAll(request: DFRequestV1): Boolean = shouldShowAll(request.result.parameters.category)

  def shouldShowAll(request: DFRequestV2): Boolean = shouldShowAll(request.queryResult.parameters.category)

  private def shouldShowAll(categories: List[String]): Boolean = categories.size == 1 && categories.contains(allCategories)
}
