lazy val echoHttpd = project in file(".")

name := "echo-httpd"

Common.settings

libraryDependencies ++= Dependencies.echoHttpd

initialCommands := """|import de.heikoseeberger.echohttpd._""".stripMargin
