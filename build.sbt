lazy val echoHttpd = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging, SbtReactiveRuntime)

name := "echo-httpd"

libraryDependencies ++= List(
  Library.akkaHttp,
  Library.scopt
)

initialCommands := """|import de.heikoseeberger.echohttpd._""".stripMargin

ReactiveRuntimeKeys.nrOfCpus := 1.0
ReactiveRuntimeKeys.memory:= 10000000
ReactiveRuntimeKeys.diskSpace := 10000000
