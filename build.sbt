import Dependencies.versions
name := "posters"

lazy val commonSettings = Seq(
  version := "3.0.0",
  scalaVersion := "2.12.7",
  scalacOptions := Seq(
    "-feature",
    "-encoding",
    "-deprecation",
    "UTF-8",
    "-language:higherKinds",
    "-language:existentials",
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Xmacro-settings:print-codecs"
  ),
  resolvers ++= Seq(
    "Twitter Maven" at "https://maven.twttr.com",
    Resolver.bintrayRepo("jmcardon", "tsec"),
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"
  )
)

lazy val root = (project in file(".")).aggregate(interface, internal, web, worker)

lazy val interface = (project in file("interface")).settings(
  commonSettings
)

lazy val internal = (project in file("internal"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.internalM
  )
  .dependsOn(interface)

lazy val web = (project in file("web"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.jsoniter,
    libraryDependencies ++= Dependencies.doobie,
    libraryDependencies ++= Dependencies.ciris,
    libraryDependencies ++= Dependencies.http4s,
    libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.25", "org.slf4j" % "slf4j-simple" % "1.7.25"),
    libraryDependencies += "org.postgresql"      % "postgresql"          % versions.postgres
  )
  .dependsOn(internal)
  .enablePlugins(JavaAppPackaging)

lazy val worker = (project in file("worker"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.jsoniter,
    libraryDependencies ++= Dependencies.doobie,
    libraryDependencies ++= Dependencies.ciris,
    libraryDependencies ++= Dependencies.workerSpecific,
    libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.25", "org.slf4j" % "slf4j-simple" % "1.7.25")
  )
  .dependsOn(internal)
  .enablePlugins(JavaAppPackaging)
