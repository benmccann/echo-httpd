lazy val echoHttpd = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging, SbtReactiveRuntime)

name := "echo-httpd"

libraryDependencies ++= List(
  Library.akkaHttp,
  Library.scopt
)

initialCommands := """|import de.heikoseeberger.echohttpd._""".stripMargin

ReactiveRuntimeKeys.cpusRequired := 1.0
ReactiveRuntimeKeys.memoryRequired := 10000000
ReactiveRuntimeKeys.totalFileSize := 10000000
