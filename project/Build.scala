import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import scalanativecrossproject.ScalaNativeCrossPlugin.autoImport._
import sbt.Keys._
import sbt._
import com.typesafe.sbt.osgi.SbtOsgi
import com.typesafe.sbt.osgi.SbtOsgi.autoImport._
import scalajscrossproject.JSPlatform
import sbtcrossproject.JVMPlatform
import dotty.tools.sbtplugin.DottyPlugin.autoImport._

object Versions {
  val Scala12 = "2.12.12" // Don't use 2.12 yet to avoid troubles with native
  val Scala13 = "2.13.3"
  val Scala11 = "2.11.12"
  val Dotty   = "0.26.0-RC1"

  val scalaJSVersion =
    Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.33")
  
  val ScalaCross =
    Seq(Scala11, Scala12, Scala13, Dotty)
  
  def crossScalaVersions(platform: sbtcrossproject.Platform) = platform match {
    case NativePlatform => Seq(Scala11)
    case JSPlatform     => ScalaCross.filterNot(_ == Dotty)
    case JVMPlatform    => ScalaCross
  }


  val ScalaTest = "3.2.2"
  val ScalaCheck = "1.14.3"
  val Json4s = "3.6.9"
}

object Dependencies {
  val scalaTest = Def.setting(
    Seq("org.scalatest" %%% "scalatest" % Versions.ScalaTest % Test)//.map(_.withDottyCompat(scalaVersion.value))
  )
  val scalaCheck = Def.setting(
    Seq("org.scalacheck" %%% "scalacheck" % Versions.ScalaCheck % Test).map(_.withDottyCompat(scalaVersion.value))
  )
  val json4s = Def.setting(
    Seq("org.json4s" %% "json4s-native" % Versions.Json4s % Test).map(_.withDottyCompat(scalaVersion.value))
  )
}

object Resolvers {
  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"
  val sonatypeNexusStaging = "Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}

object Project {
  val defaultSettings = Seq(
    name := "Squants",

    autoAPIMappings := true,

    resolvers ++= Seq(
        Resolvers.sonatypeNexusSnapshots,
        Resolvers.sonatypeNexusReleases,
        Resolvers.sonatypeNexusStaging
    ),

    OsgiKeys.exportPackage := Seq("squants.*"),

    OsgiKeys.privatePackage := Seq() // No private packages
  )
}

object Compiler {
  lazy val newerCompilerLintSwitches = Seq(
    "-Xlint:missing-interpolator",
    "-Ywarn-unused",
    "-Ywarn-numeric-widen",
    "-deprecation:false"
    
  )

  lazy val defaultCompilerSwitches = Seq(
    "-feature",
    "-deprecation",
    "-encoding", "UTF-8",       // yes, this is 2 args
    "-Xfatal-warnings",
    "-unchecked",
    "-language:implicitConversions"
  )

  lazy val dottyRemovedSwitches = Seq(
    "-Xfuture",
    "-Ywarn-dead-code"
  )

  lazy val defaultSettings = Seq(
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-encoding", "UTF-8",
    ),
    scalacOptions := {CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor >= 13 => defaultCompilerSwitches ++ newerCompilerLintSwitches
      case Some((2, scalaMajor)) if scalaMajor >= 11 => defaultCompilerSwitches ++ newerCompilerLintSwitches :+ "-Ywarn-unused-import"
      case Some((2, _))                              => defaultCompilerSwitches ++ dottyRemovedSwitches
      case _ => defaultCompilerSwitches
    }}.distinct,

    scalaVersion := Versions.Scala12
  )

}

object Publish {
  val defaultSettings = Seq(
    publishArtifact in Test := false
  )
}

object Tests {
  val defaultSettings =
    Seq(
      libraryDependencies ++=
        Dependencies.scalaTest.value ++
        Dependencies.scalaCheck.value ++
        Dependencies.json4s.value
    )
}

object Formatting {
  import com.typesafe.sbt.SbtScalariform._
  import com.typesafe.sbt.SbtScalariform.autoImport.scalariformAutoformat

  lazy val defaultSettings = Seq(
    ScalariformKeys.autoformat := false,
    ScalariformKeys.preferences in Compile := defaultPreferences,
    ScalariformKeys.preferences in Test := defaultPreferences
  )

  val defaultPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignParameters, false)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(CompactControlReadability, true)
      .setPreference(CompactStringConcatenation, false)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(FormatXml, true)
      .setPreference(IndentLocalDefs, false)
      .setPreference(IndentPackageBlocks, true)
      .setPreference(IndentSpaces, 2)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PreserveSpaceBeforeArguments, false)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpacesWithinPatternBinders, true)
  }
}

object Console {
  val defaultSettings = Seq(
  scalacOptions in (Compile, console) ~= (_ filterNot (Set("-Xfatal-warnings", "-Ywarn-unused-import").contains)),

  // initialCommands in console := """
  //    import scala.language.postfixOps,
  //        squants._,
  //        squants.energy._,
  //        squants.electro._,
  //        squants.information._,
  //        squants.market._,
  //        squants.mass._,
  //        squants.motion._,
  //        squants.photo._,
  //        squants.radio._,
  //        squants.space._,
  //        squants.thermal._,
  //        squants.time._,
  //        squants.experimental.formatter._,
  //        squants.experimental.unitgroups.UnitGroup,
  //        squants.DimensionlessConversions._,
  //        squants.electro.CapacitanceConversions._,
  //        squants.electro.ConductivityConversions._,
  //        squants.electro.ElectricalConductanceConversions._,
  //        squants.electro.ElectricalResistanceConversions._,
  //        squants.electro.ElectricChargeConversions._,
  //        squants.electro.ElectricCurrentConversions._,
  //        squants.electro.ElectricPotentialConversions._,
  //        squants.electro.InductanceConversions._,
  //        squants.electro.MagneticFluxConversions._,
  //        squants.electro.MagneticFluxDensityConversions._,
  //        squants.electro.ResistivityConversions._,
  //        squants.energy.EnergyConversions._,
  //        squants.energy.EnergyDensityConversions._,
  //        squants.energy.PowerConversions._,
  //        squants.energy.PowerRampConversions._,
  //        squants.energy.SpecificEnergyConversions._,
  //        squants.information.InformationConversions._,
  //        squants.market.MoneyConversions._,
  //        squants.mass.AreaDensityConversions._,
  //        squants.mass.ChemicalAmountConversions._,
  //        squants.mass.DensityConversions._,
  //        squants.mass.MassConversions._,
  //        squants.motion.AccelerationConversions._,
  //        squants.motion.AngularVelocityConversions._,
  //        squants.motion.ForceConversions._,
  //        squants.motion.JerkConversions._,
  //        squants.motion.MassFlowConversions._,
  //        squants.motion.MomentumConversions._,
  //        squants.motion.PressureConversions._,
  //        squants.motion.VelocityConversions._,
  //        squants.motion.VolumeFlowConversions._,
  //        squants.motion.YankConversions._,
  //        squants.photo.IlluminanceConversions._,
  //        squants.photo.LuminanceConversions._,
  //        squants.photo.LuminousEnergyConversions._,
  //        squants.photo.LuminousExposureConversions._,
  //        squants.photo.LuminousFluxConversions._,
  //        squants.photo.LuminousIntensityConversions._,
  //        squants.radio.IrradianceConversions._,
  //        squants.radio.RadianceConversions._,
  //        squants.radio.RadiantIntensityConversions._,
  //        squants.radio.SpectralIntensityConversions._,
  //        squants.radio.SpectralPowerConversions._,
  //        squants.space.AngleConversions._,
  //        squants.space.AreaConversions._,
  //        squants.space.LengthConversions._,
  //        squants.space.SolidAngleConversions._,
  //        squants.space.VolumeConversions._,
  //        squants.thermal.TemperatureConversions._,
  //        squants.thermal.ThermalCapacityConversions._,
  //        squants.time.FrequencyConversions._,
  //        squants.time.TimeConversions._""".stripMargin
  )
}

object Docs {
  private def gitHash = sys.process.Process("git rev-parse HEAD").lineStream_!.head
  val defaultSettings = Seq(
    scalacOptions in (Compile, doc) ++= {
      val (bd, v) = ((baseDirectory in LocalRootProject).value, version.value)
      val tagOrBranch = if(v endsWith "SNAPSHOT") gitHash else "v" + v
      Seq("-sourcepath", bd.getAbsolutePath, "-doc-source-url", "https://github.com/garyKeorkunian/squants/tree/" + tagOrBranch + "€{FILE_PATH}.scala")
    },
  )
}
