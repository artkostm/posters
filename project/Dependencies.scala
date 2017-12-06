import sbt._

object Dependencies {
  lazy val versions = new {
    val finatra  = "17.11.0"
    val logback  = "1.2.3"
    val scraper  = "2.0.0"
    val akka     = "2.5.6"
    val slick    = "3.2.1"
    val slick_pg = "0.15.4"
    val hikaricp = "3.2.1"
    val postgres = "9.4.1208"
  }

  val finatra_http    = "com.twitter"         %%  "finatra-http"       % versions.finatra
  val logback         = "ch.qos.logback"      %   "logback-classic"    % versions.logback
  val scraper         = "net.ruippeixotog"    %%  "scala-scraper"      % versions.scraper
  val akka_actor      = "com.typesafe.akka"   %   "akka-actor_2.12"    % versions.akka
  val slick           = "com.typesafe.slick"  %%  "slick"              % versions.slick
  val slick_pg        = "com.github.tminglei" %%  "slick-pg"           % versions.slick_pg
  val slick_pg_play = "com.github.tminglei" %%  "slick-pg_play-json" % versions.slick_pg
  val hikaricp        = "com.typesafe.slick"  %%  "slick-hikaricp"     % versions.hikaricp
  val postgres        = "org.postgresql"      %   "postgresql"         % versions.postgres
  val akka_streams    = "com.typesafe.akka"   %%  "akka-stream"        % versions.akka


  lazy val all = Seq(finatra_http, logback, scraper, akka_actor, slick,
    slick_pg, slick_pg_play, hikaricp, postgres, akka_streams)
}
