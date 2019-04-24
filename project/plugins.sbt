addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.17")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

resolvers += Resolver.bintrayRepo("kamon-io", "sbt-plugins")
addSbtPlugin("io.kamon" % "sbt-aspectj-runner" % "1.1.2")