import sbt._

object Dependencies {
  val versions = new {
    val scraper          = "2.1.0"
    val hikaricp         = "3.2.3"
    val postgres         = "42.2.5"
    val ciris            = "0.12.1"
    val doobie           = "0.7.0"
    val http4s           = "0.20.3"
    val jsoniter         = "0.41.0"
    val flyway           = "5.1.4"
    val tsec             = "0.1.0-M2"
    val fs2              = "1.0.5"
    val slf4j            = "1.7.25"
    val kindProjector    = "0.9.9"
    val betterMonadicFor = "0.3.0-M4"
    val catsCommon       = "1.6.1"
    val catsEffect       = "1.3.1"
    val logback            = "1.2.3"
    val scalalogging = "3.9.2"

    val scalaTest         = "3.0.7"
    val scalaCheck        = "1.14.0"
    val scalaMock         = "4.1.0"
    val testcontainers    = "0.26.0"
    val postgresContainer = "1.11.2"
  }

  lazy val ciris = Seq(
    "is.cir" %% "ciris-core",
    "is.cir" %% "ciris-enumeratum",
    "is.cir" %% "ciris-refined",
    "is.cir" %% "ciris-cats-effect",
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

  lazy val kamon = Seq(
    "io.kamon" %% "kamon-core"           % "1.1.6",
    "io.kamon" %% "kamon-http4s"         % "1.0.12",
    "io.kamon" %% "kamon-prometheus"     % "1.1.1",
    "io.kamon" %% "kamon-system-metrics" % "1.0.1",
    "io.kamon" %% "kamon-jdbc"           % "1.0.2"
  )

  lazy val workerSpecific = Seq(
    "org.flywaydb"   % "flyway-core" % versions.flyway,
    "co.fs2"         %% "fs2-core"   % versions.fs2,
    "org.postgresql" % "postgresql"  % versions.postgres,
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "com.typesafe.scala-logging" %% "scala-logging" % versions.scalalogging
  )

  lazy val internalM = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"    % versions.jsoniter % Provided,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros"  % versions.jsoniter % Provided,
    "org.postgresql"                        % "postgresql"              % versions.postgres % Provided,
    "net.ruippeixotog"                      %% "scala-scraper"          % versions.scraper
  ) ++ ciris.map(_                          % Provided) ++ doobie.map(_ % Provided) ++ http4s.map(_ % Provided)

  lazy val webSpecific = Seq(
    "io.github.jmcardon" %% "tsec-jwt-mac" % versions.tsec,
    "org.postgresql"     % "postgresql"    % versions.postgres
  ) ++ kamon

  lazy val logging = Seq(
    "org.slf4j" % "slf4j-api"    % versions.slf4j,
    "org.slf4j" % "slf4j-simple" % versions.slf4j
  )

  lazy val kindProjector    = "org.spire-math" %% "kind-projector"     % versions.kindProjector
  lazy val betterMonadicFor = "com.olegpy"     %% "better-monadic-for" % versions.betterMonadicFor

  lazy val commonTest = Seq(
    "org.scalatest"  %% "scalatest"  % versions.scalaTest,
    "org.scalacheck" %% "scalacheck" % versions.scalaCheck,
    "org.scalamock"  %% "scalamock"  % versions.scalaMock,
  )

  lazy val integTests = commonTest ++ Seq(
    "com.dimafeng"       %% "testcontainers-scala" % versions.testcontainers,
    "org.testcontainers" % "postgresql"            % versions.postgresContainer,
    "org.tpolecat"       %% "doobie-scalatest"     % versions.doobie,
  )

  lazy val commonOverrides = Seq(
    "org.typelevel" %% "cats-effect" % versions.catsEffect,
    "org.typelevel" %% "cats-core"   % versions.catsCommon,
    "org.typelevel" %% "cats-macros" % versions.catsCommon,
    "co.fs2"        %% "fs2-core"    % versions.fs2,
  )
}
