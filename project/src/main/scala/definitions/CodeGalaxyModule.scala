package definitions

import common.Libs
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule

trait CodeGalaxyModule extends CommonModule {

  override val repoName = "codegalaxy-app"

  val scommonsReactNativeVersion: String = Libs.scommonsReactNativeVersion

  override def definition: Project = {
    super.definition
      .settings(CodeGalaxyModule.settings: _*)
  }
}

object CodeGalaxyModule {

  val settings: Seq[Setting[_]] = Seq(
    organization := "io.codegalaxy.app",

    Test / parallelExecution := false
  )
}
