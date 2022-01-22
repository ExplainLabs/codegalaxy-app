package definitions

import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonMobileModule
import scoverage.ScoverageKeys.{coverageEnabled, coverageScalacPluginVersion}
import scoverage.ScoverageSbtPlugin._

trait ScalaJsModule extends CodeGalaxyModule with CommonMobileModule {

  override def definition: Project = {
    super.definition
      .settings(ScalaJsModule.settings: _*)
  }
}

object ScalaJsModule {

  val settings: Seq[Setting[_]] = Seq(
    // required for node.js >= v12.12.0
    // see:
    //   https://github.com/nodejs/node/pull/29919
    jsEnv in Test := new NodeJSEnv(NodeJSEnv.Config().withArgs(List("--enable-source-maps"))),
    scalaJSLinkerConfig in Test ~= {
      _.withSourceMap(true)
    },

    //TODO: remove these temporal fixes for Scala.js 1.1+ and scoverage
    coverageScalacPluginVersion := {
      val current = coverageScalacPluginVersion.value
      if (scalaJSVersion.startsWith("0.6")) current
      else "1.4.2" //the only version that supports Scala.js 1.1+
    },
    libraryDependencies ~= { modules =>
      if (scalaJSVersion.startsWith("0.6")) modules
      else modules.filter(_.organization != OrgScoverage)
    },
    libraryDependencies ++= {
      if (coverageEnabled.value) {
        if (scalaJSVersion.startsWith("0.6")) Nil
        else Seq(
          OrgScoverage %% s"${ScalacRuntimeArtifact}_sjs1" % coverageScalacPluginVersion.value,
          OrgScoverage %% ScalacPluginArtifact % coverageScalacPluginVersion.value % ScoveragePluginConfig.name
        )
      }
      else Nil
    }
  )
}
