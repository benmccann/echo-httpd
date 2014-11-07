import sbt._

object Version {
  val akka      = "2.3.6"
  val akkaHttp  = "0.10-M1"
  val logback   = "1.1.2"
  val scala     = "2.11.4"
  val scopt     = "3.2.0"
  val scalaTest = "2.2.2"
  val scalactic = "2.2.2"
}

object Library {
  val akkaActor      = "com.typesafe.akka" %% "akka-actor"             % Version.akka
  val akkaHttp       = "com.typesafe.akka" %% "akka-http-experimental" % Version.akkaHttp
  val akkaSlf4j      = "com.typesafe.akka" %% "akka-slf4j"             % Version.akka
  val akkaTestkit    = "com.typesafe.akka" %% "akka-testkit"           % Version.akka
  val logbackClassic = "ch.qos.logback"    %  "logback-classic"        % Version.logback
  val scopt          = "com.github.scopt"  %% "scopt"                  % Version.scopt
  val scalaTest      = "org.scalatest"     %% "scalatest"              % Version.scalaTest
  val scalactic      = "org.scalactic"     %% "scalactic"              % Version.scalactic
}
