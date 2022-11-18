package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.{CrossProject, JVMPlatform}
import scommons.sbtplugin.project.CommonModule
import scoverage.ScoverageKeys.coverageExcludedPackages

import scalajscrossproject.ScalaJSCrossPlugin.autoImport._

object CodeGalaxyApi {

  val id: String = "codegalaxy-api"

  val base: File = file("api")

  private lazy val `codegalaxy-api`: CrossProject = CrossProject(id, base)(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .settings(CommonModule.settings: _*)
    .settings(CodeGalaxyModule.settings: _*)
    .settings(
      coverageExcludedPackages := "io.codegalaxy.api.CodeGalaxyApiClient",
      
      libraryDependencies ++= Seq(
        Libs.scommonsApiCore.value,
        Libs.scommonsApiJodaTime.value,
        TestLibs.scalaTestJs.value % "test"
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      ScalaJsModule.settings ++ Seq(
        libraryDependencies ++= Seq(
          Libs.scalaJsJavaSecureRandom.value % "test"
        )
      ): _*
    )

  lazy val jvm: Project = `codegalaxy-api`.jvm

  lazy val js: Project = `codegalaxy-api`.js
}
