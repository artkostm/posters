package com.artkostm.posters.scraper

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupElement
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model._

object Scraper extends App {
  val browser = JsoupBrowser()

  val doc = browser.get("https://afisha.tut.by/day/2017/11/18/")

  val schedule = doc >?> element("#events-content")

  schedule match {
    case Some(JsoupElement(text)) => println(text)
    case None => println("there is no element of such class")
  }
}
