import sbt._

object Dependencies {
  lazy val versions = new {
    val finatra  = "18.5.0"
    val logback  = "1.2.3"
    val scraper  = "2.0.0"
    val akka     = "2.5.6"
    val slick    = "3.2.3"
    val slick_pg = "0.16.1"
    val hikaricp = "3.2.3"
    val postgres = "9.4.1208"
    val finatra_swagger  = "18.4.0"
    val sangria = "1.4.0"
    val tsecV = "0.0.1-M11"
  }

  val finatra_http    = "com.twitter"          %%  "finatra-http"       % versions.finatra
  val logback         = "ch.qos.logback"       %   "logback-classic"    % versions.logback
  val scraper         = "net.ruippeixotog"     %%  "scala-scraper"      % versions.scraper
  val akka_actor      = "com.typesafe.akka"    %   "akka-actor_2.12"    % versions.akka
  val slick           = "com.typesafe.slick"   %%  "slick"              % versions.slick
  val slick_pg        = "com.github.tminglei"  %%  "slick-pg"           % versions.slick_pg
  val slick_pg_play   = "com.github.tminglei"  %%  "slick-pg_play-json" % versions.slick_pg
  val hikaricp        = "com.typesafe.slick"   %%  "slick-hikaricp"     % versions.hikaricp
  val postgres        = "org.postgresql"       %   "postgresql"         % versions.postgres
  val akka_streams    = "com.typesafe.akka"    %%  "akka-stream"        % versions.akka
  val swagger         = "com.jakehschwartz"    %%  "finatra-swagger"    % versions.finatra_swagger
  val sangria         = "org.sangria-graphql"  %%  "sangria"            % versions.sangria
  val tsec_jwt_sig    = "io.github.jmcardon"   %%  "tsec-jwt-sig"       % versions.tsecV
  val tsec_jwt_mac    = "io.github.jmcardon"   %% "tsec-jwt-mac"        % versions.tsecV


  lazy val all = Seq(finatra_http, logback, scraper, akka_actor, slick,
    slick_pg, slick_pg_play, hikaricp, postgres, akka_streams, swagger, sangria, tsec_jwt_sig, tsec_jwt_mac)
}
