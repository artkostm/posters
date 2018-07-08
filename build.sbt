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