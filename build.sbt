lazy val echoHttpd = project.in(file("."))

name := "echo-httpd"

libraryDependencies ++= List(
)

initialCommands := """|import de.heikoseeberger.echohttpd._""".stripMargin
