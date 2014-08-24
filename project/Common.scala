import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.SbtScalariform._
import sbt._
import sbt.Keys._
import scalariform.formatter.preferences._

object Common {

  val settings =
    scalariformSettings ++
    packageArchetype.java_server ++ List(
      // Core settings
      organization := "de.heikoseeberger",
      version := "1.0.0",
      scalaVersion := Version.scala,
      crossScalaVersions := List(scalaVersion.value),
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.7",
        "-encoding", "UTF-8"
      ),
      unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value),
      unmanagedSourceDirectories in Test := List((scalaSource in Test).value),
      // Scalariform settings
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(PreserveDanglingCloseParenthesis, true),
      // Native packager settings
      NativePackagerKeys.dockerRepository := Some("hseeberger"),
      NativePackagerKeys.maintainer := "Heiko Seeberger <mail@heikoseeberger.de>",
      NativePackagerKeys.dockerBaseImage := "hseeberger/java",
      NativePackagerKeys.dockerExposedPorts in Docker := List(8080)
    )
}
