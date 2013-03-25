seq(webSettings :_*)

organization := "com.codeandmagic"

name := "ukgist"

version := "0.1-SNAPSHOT"

crossPaths := false

resolvers ++= Seq(
    "moved-scala-tools-releases" at "https://oss.sonatype.org/content/groups/scala-tools/",
    "moved-scala-tools-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "snapshots-repo" at "http://scala-tools.org/repo-snapshots",
    "java-net" at "http://download.java.net/maven/2",
    "thirdparty" at "http://codeandmagic.org/nexus/content/repositories/thirdparty"
)

libraryDependencies ++= Seq(
  "net.liftweb" % "lift-webkit_2.9.2" % "2.5-RC1" % "compile",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile",
  "org.apache.httpcomponents" % "httpclient" % "4.2.3" % "compile",
  "com.javadocmd" % "simplelatlng" % "1.1.0" % "compile",
  "de.micromata.jak" % "JavaAPIforKml" % "2.2.0-SNAPSHOT" % "compile",
  "com.vividsolutions" % "jts" % "1.13" % "compile",
  "org.orbroker" % "orbroker_2.9.2" % "3.2.1-1" % "compile",
  "mysql" % "mysql-connector-java" % "5.1.18" % "container",
  "org.eclipse.jetty" % "jetty-webapp" % "7.6.0.RC5" % "container",
  "org.eclipse.jetty" % "jetty-servlets" % "7.6.0.RC5" % "container",
  "org.specs2" %% "specs2" % "1.12.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test" 
)

/** Compilation */
scalaVersion := "2.9.2"

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
