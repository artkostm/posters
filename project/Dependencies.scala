import sbt._

object Dependencies {
  val versions = new {
    val scraper         = "2.1.0"
    val hikaricp        = "3.2.3"
    val postgres        = "9.4.1208"
    val ciris           = "0.12.1"
    val doobie          = "0.6.0"
    val http4s          = "0.20.0-M6"
    val jsoniter        = "0.41.0"
    val flyway          = "5.1.4"
    val tsec = "0.1.0-M2"
    val fs2 = "1.0.3"
    val monocle = "1.5.0" // is it really needed???
    val slf4j = "1.7.25"
    
  }

  val scraper       = "net.ruippeixotog"    %% "scala-scraper"      % versions.scraper
  val hikaricp      = "com.typesafe.slick"  %% "slick-hikaricp"     % versions.hikaricp
  val postgres      = "org.postgresql"      % "postgresql"          % versions.postgres

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
    "co.fs2"       %% "fs2-core"   % versions.fs2,
    "org.postgresql"     % "postgresql"    % versions.postgres
  )

  lazy val internalM = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"    % versions.jsoniter % Provided,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros"  % versions.jsoniter % Provided,
    "org.postgresql"                        % "postgresql"              % versions.postgres % Provided,
    "net.ruippeixotog"                      %% "scala-scraper"          % versions.scraper,
    "com.lihaoyi"                           %% "fastparse"              % "2.1.0"
  ) ++ ciris.map(_                          % Provided) ++ doobie.map(_ % Provided)
  
  lazy val webSpecific = Seq(
    "io.github.jmcardon" %% "tsec-jwt-mac" % versions.tsec,
    "org.postgresql"     % "postgresql"    % versions.postgres,
    "com.github.julien-truffaut" %%  "monocle-core"  % versions.monocle
  )
  
  lazy val logging = Seq(
    "org.slf4j" % "slf4j-api" % versions.slf4j, 
    "org.slf4j" % "slf4j-simple" % versions.slf4j
  )
}
