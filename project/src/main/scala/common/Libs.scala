package common

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._
import scommons.sbtplugin.project.CommonLibs

object Libs extends CommonLibs {
  
  val scommonsReactVersion = "1.0.0-SNAPSHOT"
  val scommonsReactNativeVersion = "1.0.0-SNAPSHOT"
  val scommonsApiVersion = "1.0.0-SNAPSHOT"

  //////////////////////////////////////////////////////////////////////////////
  // shared dependencies

  lazy val scommonsApiCore = Def.setting("org.scommons.api" %%% "scommons-api-core" % scommonsApiVersion)
  lazy val scommonsApiJodaTime = Def.setting("org.scommons.api" %%% "scommons-api-joda-time" % scommonsApiVersion)

  //////////////////////////////////////////////////////////////////////////////
  // js dependencies

  lazy val scommonsReactNativeUi = Def.setting("org.scommons.react-native" %%% "scommons-react-native-ui" % scommonsReactNativeVersion)

}
