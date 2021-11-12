package definitions

import common.Libs
import sbt.Keys._
import sbt._
import scommons.sbtplugin.ScommonsPlugin.autoImport._
import scommons.sbtplugin.project.CommonMobileModule
import scoverage.ScoverageKeys.coverageExcludedPackages

object CodeGalaxyApp extends CodeGalaxyModule with CommonMobileModule {

  override val id = "codegalaxy-app"

  override val base: File = file("app")

  override def definition: Project = super.definition
    .settings(
      description := "Mobile app for CodeGalaxy.io, written in Scala.js",

      scommonsBundlesFileFilter := "*.sql",

      coverageExcludedPackages :=
        "io.codegalaxy.app.CodeGalaxyApp" +
          ";io.codegalaxy.app.CodeGalaxyActions" +
          ";io.codegalaxy.app.CodeGalaxyIcons" +
          ";common.reactnative.raw",

      // we substitute references to react-native modules with our custom mocks during test
      scommonsNodeJsTestLibs := Seq(
        "scommons.reactnative.aliases.js",
        "common.aliases.js"
      )
    )

  override def internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    CodeGalaxyApi.js,
    CodeGalaxyDao.definition
  )

  override def superRepoProjectsDependencies: Seq[(String, String, Option[String])] = {
    super.superRepoProjectsDependencies ++ Seq(
      ("scommons-react-native", "scommons-react-native-ui", None)
    )
  }
  
  override def runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.runtimeDependencies.value ++ Seq(
      Libs.scommonsReactNativeUi.value
    )
  }
  
  override def testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting {
    super.testDependencies.value ++ Seq[ModuleID](
      // specify your custom test dependencies here
    ).map(_ % "test")
  }
}
