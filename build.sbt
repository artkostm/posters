lazy val commonSettings = Seq(
  version := "3.0.0",
  scalaVersion := "2.12.7",
  scalacOptions := Seq(
    "-feature",
    "-encoding", "UTF-8",
    "-deprecation",
    "-language:higherKinds",
    "-language:existentials",
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Xmacro-settings:print-codecs"
  ),
  resolvers ++= Seq(
    Resolver.bintrayRepo("jmcardon", "tsec"),
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
)

lazy val root = (project in file(".")).aggregate(interface, internal, web, worker)

lazy val interface = (project in file("interface")).settings(
  commonSettings
)

lazy val internal = (project in file("internal"))
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.internalM,
    dependencyOverrides ++= Dependencies.commonOverrides
  )
  .dependsOn(interface)

lazy val web = (project in file("web"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Dependencies.jsoniter,
    libraryDependencies ++= Dependencies.doobie,
    libraryDependencies ++= Dependencies.ciris,
    libraryDependencies ++= Dependencies.http4s,
    libraryDependencies ++= Dependencies.webSpecific,
    libraryDependencies ++= Dependencies.logging,
    libraryDependencies ++= unitTesting(Dependencies.commonTest),
    libraryDependencies ++= integTesting(Dependencies.integTests),
    dependencyOverrides ++= Dependencies.commonOverrides,
    addCompilerPlugin(Dependencies.kindProjector),
    addCompilerPlugin(Dependencies.betterMonadicFor)
  )
  .dependsOn(internal)
  .enablePlugins(JavaAgent, JavaAppPackaging)
  .settings(
    javaAgents += "org.aspectj" % "aspectjweaver" % "1.9.2"
  )

lazy val worker = (project in file("worker"))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Dependencies.jsoniter,
    libraryDependencies ++= Dependencies.doobie,
    libraryDependencies ++= Dependencies.ciris,
    libraryDependencies ++= Dependencies.workerSpecific,
    libraryDependencies ++= Dependencies.logging,
    libraryDependencies ++= integTesting(Dependencies.integTests),
    dependencyOverrides ++= Dependencies.commonOverrides
  )
  .dependsOn(internal)
  .enablePlugins(JavaAppPackaging)


def unitTesting(tests: Seq[ModuleID]) = tests.map(_ % Test)
def integTesting(tests: Seq[ModuleID]) = tests.map(_ % IntegrationTest)