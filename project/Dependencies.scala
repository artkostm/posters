import sbt._

object Dependencies {
  val versions = new {
    val finatra         = "18.5.0"
    val logback         = "1.2.3"
    val scraper         = "2.0.0"
    val akka            = "2.5.6"
    val slick           = "3.2.3"
    val slick_pg        = "0.16.1"
    val hikaricp        = "3.2.3"
    val postgres        = "9.4.1208"
    val finatra_swagger = "18.4.0"
    val sangria         = "1.4.0"
    val tsecV           = "0.0.1-M11"
    val ciris           = "0.10.0"
    val doobie          = "0.5.3"
    val http4s          = "0.18.14"
    val jsoniter        = "0.29.2"
    val flyway          = "5.1.4"
    val H2              = "1.4.196"
    val streamz         = "0.9.1"
  }

  val finatra_http  = "com.twitter"         %% "finatra-http"       % versions.finatra
  val logback       = "ch.qos.logback"      % "logback-classic"     % versions.logback
  val scraper       = "net.ruippeixotog"    %% "scala-scraper"      % versions.scraper
  val akka_actor    = "com.typesafe.akka"   % "akka-actor_2.12"     % versions.akka
  val slick         = "com.typesafe.slick"  %% "slick"              % versions.slick
  val slick_pg      = "com.github.tminglei" %% "slick-pg"           % versions.slick_pg
  val slick_pg_play = "com.github.tminglei" %% "slick-pg_play-json" % versions.slick_pg
  val hikaricp      = "com.typesafe.slick"  %% "slick-hikaricp"     % versions.hikaricp
  val postgres      = "org.postgresql"      % "postgresql"          % versions.postgres
  val akka_streams  = "com.typesafe.akka"   %% "akka-stream"        % versions.akka
  val swagger       = "com.jakehschwartz"   %% "finatra-swagger"    % versions.finatra_swagger
  val sangria       = "org.sangria-graphql" %% "sangria"            % versions.sangria
  val tsec_jwt_sig  = "io.github.jmcardon"  %% "tsec-jwt-sig"       % versions.tsecV
  val tsec_jwt_mac  = "io.github.jmcardon"  %% "tsec-jwt-mac"       % versions.tsecV

  lazy val all = Seq(finatra_http,
                     logback,
                     scraper,
                     akka_actor,
                     slick,
                     slick_pg,
                     slick_pg_play,
                     hikaricp,
                     postgres,
                     akka_streams,
                     swagger,
                     sangria,
                     tsec_jwt_sig,
                     tsec_jwt_mac)

  lazy val commonDependencies = Seq(
    "joda-time" % "joda-time" % "2.9.9"
  )

  lazy val cirisDependencies = Seq(
    "is.cir" %% "ciris-cats",
    "is.cir" %% "ciris-cats-effect",
    "is.cir" %% "ciris-core",
    "is.cir" %% "ciris-enumeratum",
    "is.cir" %% "ciris-generic",
    "is.cir" %% "ciris-refined",
    "is.cir" %% "ciris-spire",
    "is.cir" %% "ciris-squants"
  ).map(_ % versions.ciris)

  lazy val doobieDependencies = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres", // Postgres driver 42.2.2 + type mappings.
    "org.tpolecat" %% "doobie-scalatest"
  ).map(_ % versions.doobie)

  lazy val http4sDependencies = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server"
  ).map(_ % versions.http4s)

  lazy val jsoniterDependencies = Seq(
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % versions.jsoniter % Compile,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % versions.jsoniter % Provided
  )

  lazy val workerSpecificDependencies = Seq(
    "com.typesafe.akka"   %% "akka-actor"        % versions.akka,
    "net.ruippeixotog"    %% "scala-scraper"     % versions.scraper,
    "org.flywaydb"        % "flyway-core"        % versions.flyway,
    "com.h2database"      % "h2"                 % versions.H2,
    "org.tpolecat"        %% "doobie-h2"         % versions.doobie,
    "com.github.krasserm" %% "streamz-converter" % versions.streamz
  )

  lazy val internalM = Seq(
    "org.tpolecat"                          %% "doobie-core"           % versions.doobie   % Provided,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % versions.jsoniter % Provided,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % versions.jsoniter % Provided,
    "org.postgresql"                        % "postgresql"             % versions.postgres % Provided
  )
}
