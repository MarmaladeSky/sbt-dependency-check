// See https://github.com/albuch/sbt-dependency-check/issues/95

name := "global-and-project-settings"
version := "0.1"F
scalaVersion := "2.11.12"

Global / dependencyCheckNvdPassword := Some("Global")
Global / dependencyCheckNvdDatafeedUser := Some("Global")
ThisBuild / dependencyCheckNvdDatafeedUser := Some("ThisBuild")

lazy val root = (project in file("."))
  .aggregate(inscope, alsoinscope)
  .settings(
    dependencyCheckNvdPassword := Some("root"),
  )

lazy val inscope = (project in file("inscope")).settings(
  dependencyCheckNvdPassword := Some("inscope")
)
lazy val alsoinscope = (project in file("alsoinscope")).settings(
  TaskKey[Unit]("depCheckAssert") := {
    val thisBuildUser = dependencyCheckNvdDatafeedUser.value
    assert( thisBuildUser.contains("ThisBuild") )
    val thisBuildPassword = dependencyCheckNvdPassword.value
    assert( thisBuildPassword.contains("Global") )
  }
)

TaskKey[Unit]("depCheckAssert") := {
  val rootPassword = dependencyCheckNvdPassword.value
  val rootInThisBuildUser = ( ThisBuild / dependencyCheckNvdDatafeedUser).value
  val rootInThisBuildPassword = (ThisBuild / dependencyCheckNvdPassword).value
  val inscopePassword = (inscope / dependencyCheckNvdPassword).value
  val alsoinscopePassword = (alsoinscope / dependencyCheckNvdPassword).value
  assert( rootPassword.contains("root") )
  assert( rootInThisBuildUser.contains("ThisBuild") )
  assert( rootInThisBuildPassword.contains("Global") )
  assert( inscopePassword.contains("inscope") )
  assert( alsoinscopePassword.contains("Global") )
}