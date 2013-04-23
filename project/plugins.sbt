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

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.8")

