import sbt._

object Dependencies {
  lazy val versions = new {
    val finatra = "17.11.0"
    val logback = "1.2.3"
    val scraper = "2.0.0"
    val akka = "2.5.6"
    val mapdb = "3.0.5"
  }

  val finatra_http = "com.twitter" %% "finatra-http" % versions.finatra
  val logback = "ch.qos.logback" % "logback-classic" % versions.logback
  val scraper = "net.ruippeixotog" %% "scala-scraper" % versions.scraper
  val akka_actor = "com.typesafe.akka" % "akka-actor_2.12" % versions.akka
  val mapdb = "org.mapdb" % "mapdb" % versions.mapdb
  val mapdbutils = "com.github.karasiq" %% "mapdbutils" % "1.1.1"

  lazy val finatra = Seq(finatra_http, logback, scraper, akka_actor, mapdb, mapdbutils)
}
