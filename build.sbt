lazy val echoHttpd = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)

name := "echo-httpd"

libraryDependencies ++= List(
  Library.akkaHttp,
  Library.scopt
)

initialCommands := """|import de.heikoseeberger.echohttpd._""".stripMargin
