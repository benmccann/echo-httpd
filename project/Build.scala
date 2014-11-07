import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.SbtScalariform._
import sbt._
import sbt.Keys._
import scalariform.formatter.preferences._

object Build extends AutoPlugin {

  override def requires =
    plugins.JvmPlugin

  override def trigger =
    allRequirements

  override def projectSettings =
    scalariformSettings ++
    List(
      // Core settings
      organization := "de.heikoseeberger",
      version := "1.10.1",
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
        .setPreference(AlignArguments, true)
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
        .setPreference(DoubleIndentClassDeclaration, true),
      // Native packager settings
      NativePackagerKeys.dockerRepository := Some("hseeberger"),
      NativePackagerKeys.maintainer := "Heiko Seeberger <mail@heikoseeberger.de>",
      NativePackagerKeys.dockerBaseImage := "dockerfile/java:oracle-java8"
    )
}
