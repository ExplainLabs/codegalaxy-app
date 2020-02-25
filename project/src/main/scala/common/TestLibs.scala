package common

import common.Libs._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._
import scommons.sbtplugin.project.CommonTestLibs

object TestLibs extends CommonTestLibs {

  lazy val scommonsReactTestDom = Def.setting("org.scommons.react" %%% "scommons-react-test-dom" % scommonsReactVersion)
}
