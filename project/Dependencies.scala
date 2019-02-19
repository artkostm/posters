import sbt._

object Dependencies {
  val versions = new {
//    val finatra         = "18.5.0"
//    val logback         = "1.2.3"
    val scraper         = "2.0.0"
//    val akka            = "2.5.6"
//    val slick           = "3.2.3"
//    val slick_pg        = "0.16.1"
    val hikaricp        = "3.2.3"
    val postgres        = "9.4.1208"
//    val finatra_swagger = "18.4.0"
//    val sangria         = "1.4.0"
//    val tsecV           = "0.0.1-M11"
    val ciris           = "0.12.1"
    val doobie          = "0.6.0"
    val http4s          = "0.20.0-M5"
    val jsoniter        = "0.39.0"
    val flyway          = "5.1.4"
//    val streamz         = "0.9.1"
  }

//  val finatra_http  = "com.twitter"         %% "finatra-http"       % versions.finatra
//  val logback       = "ch.qos.logback"      % "logback-classic"     % versions.logback
  val scraper       = "net.ruippeixotog"    %% "scala-scraper"      % versions.scraper
//  val akka_actor    = "com.typesafe.akka"   % "akka-actor_2.12"     % versions.akka
//  val slick         = "com.typesafe.slick"  %% "slick"              % versions.slick
//  val slick_pg      = "com.github.tminglei" %% "slick-pg"           % versions.slick_pg
//  val slick_pg_play = "com.github.tminglei" %% "slick-pg_play-json" % versions.slick_pg
  val hikaricp      = "com.typesafe.slick"  %% "slick-hikaricp"     % versions.hikaricp
  val postgres      = "org.postgresql"      % "postgresql"          % versions.postgres
//  val akka_streams  = "com.typesafe.akka"   %% "akka-stream"        % versions.akka
//  val swagger       = "com.jakehschwartz"   %% "finatra-swagger"    % versions.finatra_swagger
//  val sangria       = "org.sangria-graphql" %% "sangria"            % versions.sangria
//  val tsec_jwt_sig  = "io.github.jmcardon"  %% "tsec-jwt-sig"       % versions.tsecV
//  val tsec_jwt_mac  = "io.github.jmcardon"  %% "tsec-jwt-mac"       % versions.tsecV

  lazy val ciris = Seq(
    "is.cir" %% "ciris-core",
    "is.cir" %% "ciris-enumeratum",
    "is.cir" %% "ciris-refined",
  ).map(_ % versions.ciris)

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % versions.doobie)

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-core",
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server"
  ).map(_ % versions.http4s)

  lazy val jsoniter = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % versions.jsoniter % Compile,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % versions.jsoniter % Provided
  )

  lazy val workerSpecific = Seq(
    "org.flywaydb" % "flyway-core" % versions.flyway,
    "co.fs2"       %% "fs2-core"   % "1.0.3"
  )

  lazy val internalM = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"    % versions.jsoniter % Provided,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros"  % versions.jsoniter % Provided,
    "org.postgresql"                        % "postgresql"              % versions.postgres % Provided,
    "net.ruippeixotog"                      %% "scala-scraper"          % versions.scraper,
    "com.lihaoyi"                           %% "fastparse"              % "2.1.0"
  ) ++ ciris.map(_                          % Provided) ++ doobie.map(_ % Provided)
}
