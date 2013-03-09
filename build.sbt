seq(webSettings :_*)

organization := "org.codeandmagic"

name := "ukgist"

version := "0.1-SNAPSHOT"

crossPaths := false

resolvers ++= Seq(
    "moved-scala-tools-releases" at "https://oss.sonatype.org/content/groups/scala-tools/",
    "moved-scala-tools-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "snapshots-repo" at "http://scala-tools.org/repo-snapshots",
    "thirdparty" at "http://codeandmagic.org/nexus/content/repositories/thirdparty"
)

libraryDependencies ++= Seq(
  "net.liftweb" % "lift-webkit_2.10" % "2.5-RC1" % "compile",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile",
  "org.apache.httpcomponents" % "httpclient" % "4.1.2" % "compile",
  "mysql" % "mysql-connector-java" % "5.1.18" % "compile",
  "org.eclipse.jetty" % "jetty-webapp" % "7.6.0.RC5" % "container",
  "org.eclipse.jetty" % "jetty-servlets" % "7.6.0.RC5" % "container",
  "org.specs2" %% "specs2" % "1.12.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test" 
)

/** Compilation */
javacOptions ++= Seq()

javaOptions += "-Xmx1G"

scalacOptions ++= Seq("-deprecation", "-unchecked")

maxErrors := 20 

pollInterval := 1000

logBuffered := false

cancelable := true

testOptions := Seq(Tests.Filter(s =>
  Seq("Spec", "Suite", "Unit", "all").exists(s.endsWith(_)) &&
    !s.endsWith("FeaturesSpec") ||
    s.contains("UserGuide") ||
    s.contains("index") ||
    s.matches("org.specs2.guide.*")))

net.virtualvoid.sbt.graph.Plugin.graphSettings
