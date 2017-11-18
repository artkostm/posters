import sbt._

object Dependencies {
  lazy val versions = new {
    val finatra = "17.11.0"
    val logback = "1.2.3"
    val scraper = "2.0.0"
  }

  val finatra_http = "com.twitter" %% "finatra-http" % versions.finatra
  val logback = "ch.qos.logback" % "logback-classic" % versions.logback
  val scraper = "net.ruippeixotog" %% "scala-scraper" % versions.scraper


  lazy val finatra = Seq(finatra_http, logback, scraper)
}
