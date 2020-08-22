import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val defaultSettings =
  Project.defaultSettings ++
  Compiler.defaultSettings ++
  Publish.defaultSettings ++
  Formatting.defaultSettings ++
  Console.defaultSettings ++
  Docs.defaultSettings

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / turbo := true

val customScalaJSVersion = Option(System.getenv("SCALAJS_VERSION"))

inThisBuild(List(
  organization := "org.typelevel",
  homepage := Some(url("http://www.squants.com/")),
  licenses := Seq("Apache 2.0" -> url("http://www.opensource.org/licenses/Apache-2.0")),
  developers := List(
    Developer(
      "garyKeorkunian",
      "Gary Keorkunian",
      "unknown",
      url("http://www.linkedin.com/in/garykeorkunian")
    )
  )
))

lazy val squants =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(defaultSettings: _*)
  .jvmConfigure(
    _.enablePlugins(MdocPlugin, SbtOsgi)
  )
  .jvmSettings(
    osgiSettings,
    scalacOptions in mdoc := Seq(),//Seq("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Xlint:_,-missing-interpolator"),
    mdocOut := file("."),
    mdocIn := file("shared") / "src" / "main" / "tut",
    parallelExecution in Test := false,
    skip.in(publish) := customScalaJSVersion.isDefined,
    crossScalaVersions := Versions.crossScalaVersions(JVMPlatform),
  )
  .jvmSettings(Tests.defaultSettings: _*)
  .jsSettings(
    parallelExecution in Test := false,
    excludeFilter in Test := "*Serializer.scala" || "*SerializerSpec.scala",
    crossScalaVersions := Versions.crossScalaVersions(JSPlatform)
  )
  .jsSettings(Tests.defaultSettings: _*)
  .nativeSettings(
    skip in publish := true,
    crossScalaVersions := Versions.crossScalaVersions(NativePlatform),
    scalaVersion := Versions.Scala11
  )

lazy val root = project.in(file("."))
  .settings(defaultSettings: _*)
  .settings(noPublishSettings)
  .settings(
    name := "squants",
  )
  .aggregate(squants.jvm, squants.js, squants.native)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)
