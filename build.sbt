name := "posters"

enablePlugins(JavaAppPackaging)

lazy val commonSettings = Seq(
  version             := "0.1.0",
  scalaVersion        := "2.12.4",
  crossScalaVersions  := Seq("2.12.4"),
  scalaVersion        := crossScalaVersions.value.head,
  scalacOptions       ++= Seq("-feature", "-deprecation", "-encoding", "utf-8", "-language:implicitConversions", "-Ypartial-unification"),
  resolvers           ++= Seq("Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots", 
    "Twitter Maven" at "https://maven.twttr.com",
    Resolver.bintrayRepo("jmcardon", "tsec"))
)

lazy val root = (project in file(".")).aggregate(interface, internal, web, worker)

lazy val interface = (project in file("interface")).settings(
  commonSettings
)

lazy val internal = (project in file("internal")).settings(
  commonSettings
)

lazy val web = (project in file("web")).settings(
  commonSettings,
  libraryDependencies ++= Dependencies.all,
  libraryDependencies ++= Dependencies.cirisDependencies,
  libraryDependencies ++= Dependencies.doobieDependencies,
  libraryDependencies ++= Dependencies.jsoniterDependencies,
  libraryDependencies ++= Dependencies.http4sDependencies
)

lazy val worker = (project in file("worker")).settings(
  commonSettings,
  libraryDependencies ++= Dependencies.all
)