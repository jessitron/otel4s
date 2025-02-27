addSbtPlugin("org.typelevel" % "sbt-typelevel" % "0.6.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-scalafix" % "0.6.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.6.7")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.17")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.7")
addSbtPlugin("com.github.sbt" % "sbt-javaagent" % "0.1.8")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")
addSbtPlugin(
  "com.armanbilge" % "sbt-scala-native-config-brew-github-actions" % "0.2.0-RC1"
)

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.15"
