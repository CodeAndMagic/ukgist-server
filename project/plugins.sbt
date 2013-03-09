resolvers ++= Seq(
	"Web plugin repo" at "http://siasia.github.com/maven2",	
    "moved-scala-tools-releases" at "https://oss.sonatype.org/content/groups/scala-tools/",
    "moved-scala-tools-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

//Following means libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.1.1-<sbt version>""
//libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

libraryDependencies <+= sbtVersion(v => v match {
	case "0.11.0" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.0-0.2.8"
	case "0.11.1" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.1-0.2.10"
	case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
	case "0.11.3" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.3-0.2.11.1"
	case x if (x.startsWith("0.12")) => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1"
})

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.6.0")
//libraryDependencies += "net.virtual-void" % "sbt-dependency-graph" % "0.6.0"

