package io.codegalaxy.app

import scommons.expo._
import scommons.reactnative.StaticResource

object CodeGalaxyIcons {

  val FontAwesome5: VectorFontComp = VectorIcons.FontAwesome5
  val Ionicons: VectorFontComp = VectorIcons.Ionicons
  
  def iconsToPreload: Seq[(String, StaticResource)] = {
    FontAwesome5.font.toSeq ++
      Ionicons.font.toSeq
  }
}
