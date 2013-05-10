//Copyright 2013 Cristian Vrabie, Evelina Vrabie
//This file is part of UKGist.
//UKGist is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//UKGist is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//You should have received a copy of the GNU General Public License
//along with UKGist.  If not, see <http://www.gnu.org/licenses/>.

import AssemblyKeys._ // put this at the top of the file

seq(assemblySettings :_*)

seq(webSettings :_*)

organization := "com.codeandmagic"

name := "ukgist"

version := "0.1-SNAPSHOT"

crossPaths := false

resolvers ++= Seq(
    "Web plugin repo" at "http://siasia.github.com/maven2",
    "moved-scala-tools-releases" at "https://oss.sonatype.org/content/groups/scala-tools/",
    "moved-scala-tools-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "snapshots-repo" at "http://scala-tools.org/repo-snapshots",
    "java-net" at "http://download.java.net/maven/2",
    "thirdparty" at "http://codeandmagic.org/nexus/content/repositories/thirdparty",
    "sourceforge" at "http://sourceforge.net/projects/jsi/files/m2_repo"
)

libraryDependencies ++= Seq(
  "net.liftweb" % "lift-webkit_2.9.2" % "2.5-RC1" % "compile",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile",
  "org.apache.httpcomponents" % "httpclient" % "4.2.3" % "compile",
  "com.javadocmd" % "simplelatlng" % "1.1.0" % "compile",
  "de.micromata.jak" % "JavaAPIforKml" % "2.2.0-SNAPSHOT" % "compile",
  "com.vividsolutions" % "jts" % "1.13" % "compile",
  "org.orbroker" % "orbroker_2.9.2" % "3.2.1-1" % "compile",
  "org.clapper" %% "classutil" % "0.4.6" % "compile",
  "com.googlecode.flyway" % "flyway-core" % "2.1.1" % "compile",
  "mysql" % "mysql-connector-java" % "5.1.24" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "7.6.0.RC5" % "container",
  "org.eclipse.jetty" % "jetty-servlets" % "7.6.0.RC5" % "container",
  "org.specs2" %% "specs2" % "1.12.4" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "com.h2database" % "h2" % "1.3.171" % "test"
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

test in assembly := {}

jarName in assembly := "ukgist-tools.jar"