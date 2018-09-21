name := "posters"

version := "0.1"

scalaVersion := "2.12.4"

enablePlugins(JavaAppPackaging)
scalacOptions += "-Ypartial-unification"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Twitter Maven" at "https://maven.twttr.com"
resolvers += Resolver.bintrayRepo("jmcardon", "tsec")

libraryDependencies ++= Dependencies.all

val cirisVersion = "0.10.0"

libraryDependencies ++= Seq(
  "is.cir" %% "ciris-cats",
  "is.cir" %% "ciris-cats-effect",
  "is.cir" %% "ciris-core",
  "is.cir" %% "ciris-enumeratum",
  "is.cir" %% "ciris-generic",
  "is.cir" %% "ciris-refined",
  "is.cir" %% "ciris-spire",
  "is.cir" %% "ciris-squants"
).map(_ % cirisVersion)

val http4sVersion = "0.18.14"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion
)

libraryDependencies ++= Seq(
  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "0.5.3",
  // And add any of these as needed
  "org.tpolecat" %% "doobie-h2"        % "0.5.3", // H2 driver 1.4.197 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % "0.5.3", // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "0.5.3", // Postgres driver 42.2.2 + type mappings.
  "org.tpolecat" %% "doobie-specs2"    % "0.5.3", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "0.5.3"  // ScalaTest support for typechecking statements.
)

libraryDependencies ++= Seq(
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "0.29.2" % Compile,
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "0.29.2" % Provided // required only in compile-time
)
