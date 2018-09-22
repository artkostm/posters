name := "posters"

lazy val commonSettings = Seq(
  version             := "0.5.0",
  scalaVersion        := "2.12.4",
  scalacOptions       ++= Seq(
    "utf-8",
    "-feature",
    "-encoding",
    "-deprecation",
    "-Ypartial-unification",
    "-language:higherKinds",
    "-language:existentials",
    "-language:implicitConversions"
  ),
  resolvers           ++= Seq(
    "Twitter Maven" at "https://maven.twttr.com",
    Resolver.bintrayRepo("jmcardon", "tsec"),
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
)

lazy val root = (project in file(".")).aggregate(interface, internal, web, worker)

lazy val interface = (project in file("interface")).settings(
  commonSettings
)

lazy val internal = (project in file("internal")).settings(
  commonSettings
).dependsOn(interface)

lazy val web = (project in file("web")).settings(
  commonSettings,
  libraryDependencies ++= Dependencies.all,
  libraryDependencies ++= Dependencies.cirisDependencies,
  libraryDependencies ++= Dependencies.doobieDependencies,
  libraryDependencies ++= Dependencies.jsoniterDependencies,
  libraryDependencies ++= Dependencies.http4sDependencies
).dependsOn(internal)
  .enablePlugins(JavaAppPackaging)

lazy val worker = (project in file("worker")).settings(
  commonSettings,
  libraryDependencies ++= Dependencies.all,
  libraryDependencies ++= Dependencies.workerSpecificDependencies
).dependsOn(internal)
  .enablePlugins(JavaAppPackaging)