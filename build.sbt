name := "posters"

version := "0.1"

scalaVersion := "2.12.4"

enablePlugins(JavaAppPackaging)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Twitter Maven" at "https://maven.twttr.com"

libraryDependencies ++= Dependencies.all
