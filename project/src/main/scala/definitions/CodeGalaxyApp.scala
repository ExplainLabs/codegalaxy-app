package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonMobileModule
import scoverage.ScoverageKeys.coverageExcludedPackages

object CodeGalaxyApp extends CodeGalaxyModule with CommonMobileModule {

  override val id = "codegalaxy-app"

  override val base: File = file("app")

  override def definition: Project = super.definition
    .settings(
      description := "Mobile app for CodeGalaxy.io, written in Scala.js",

      coverageExcludedPackages :=
        "io.codegalaxy.app.CodeGalaxyApp" +
          ";io.codegalaxy.app.CodeGalaxyActions"
    )

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    CodeGalaxyApi.js
  )

  override def superRepoProjectsDependencies: Seq[(String, String, Option[String])] = {
    super.superRepoProjectsDependencies ++ Seq(
      ("scommons-react-native", "scommons-react-native-ui", None),
      ("scommons-react-native", "scommons-expo", None),

      ("scommons-react", "scommons-react-test-dom", Some("test"))
    )
  }
  
  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.runtimeDependencies.value ++ Seq(
      Libs.scommonsReactNativeUi.value,
      Libs.scommonsExpo.value
    )
  }
  
  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.testDependencies.value ++ Seq[ModuleID](
      TestLibs.scommonsReactTestDom.value
    ).map(_ % "test")
  }
}
