name := "posters"

lazy val commonSettings = Seq(
  version := "0.5.0",
  scalaVersion := "2.12.7",
  scalacOptions := Seq(
    "-feature",
    "-encoding",
    "-deprecation",
    "UTF-8",
    "-language:higherKinds",
    "-language:existentials",
    "-language:implicitConversions",
    "-Ypartial-unification"
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
    libraryDependencies ++= Dependencies.cirisDependencies,
    libraryDependencies ++= Dependencies.internalM
  )
  .dependsOn(interface)

lazy val web = (project in file("web"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.all,
    libraryDependencies ++= Dependencies.doobieDependencies,
    libraryDependencies ++= Dependencies.jsoniterDependencies,
    libraryDependencies ++= Dependencies.http4sDependencies,
    scalacOptions ++= Seq("-Xmacro-settings:print-codecs")
  )
  .dependsOn(internal)
//.enablePlugins(JavaAppPackaging)

lazy val worker = (project in file("worker"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.all,
    libraryDependencies ++= Dependencies.workerSpecificDependencies,
    libraryDependencies ++= Dependencies.jsoniterDependencies,
    libraryDependencies ++= Dependencies.doobieDependencies,
    libraryDependencies += "com.github.alexandrnikitin" %% "bloom-filter" % "0.11.0"
  )
  .dependsOn(internal)
//.enablePlugins(JavaAppPackaging)
