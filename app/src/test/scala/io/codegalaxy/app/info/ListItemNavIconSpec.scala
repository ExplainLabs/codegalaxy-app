package io.codegalaxy.app.info

import io.codegalaxy.app.info.ListItemNavIcon._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._
import scommons.reactnative.svg._

class ListItemNavIconSpec extends TestSpec with TestRendererUtils {

  it should "render Start icon with label" in {
    //given
    val props = ListItemNavIconProps(0, showLabel = true)
    val component = <(ListItemNavIcon())(^.wrapped := props)()

    //when
    val result = testRender(component)

    //then
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.statsContainer)(
        <.Text(^.rnStyle := styles.statsLabel)("Start"),
        <.SvgXml(^.rnStyle := styles.startSvg, ^.xml := startSvgXml)()
      )
    )
  }
  
  it should "render Start icon without label" in {
    //given
    val props = ListItemNavIconProps(0, showLabel = false)
    val component = <(ListItemNavIcon())(^.wrapped := props)()

    //when
    val result = testRender(component)

    //then
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.statsContainer)(
        <.SvgXml(^.rnStyle := styles.startSvg, ^.xml := startSvgXml)()
      )
    )
  }
  
  it should "render Open icon with label" in {
    //given
    val props = ListItemNavIconProps(99, showLabel = true)
    val component = <(ListItemNavIcon())(^.wrapped := props)()

    //when
    val result = testRender(component)

    //then
    implicit val theme: Theme = DefaultTheme
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.statsContainer)(
        <.Text(^.rnStyle := styles.statsLabel)("Open"),
        <.View(^.rnStyle := styles.statsProgress)(
          <.Text(^.rnStyle := themeTextStyle)(s"${props.progress}")
        )
      )
    )
  }
  
  it should "render Open icon without label" in {
    //given
    val props = ListItemNavIconProps(99, showLabel = false)
    val component = <(ListItemNavIcon())(^.wrapped := props)()

    //when
    val result = testRender(component)

    //then
    implicit val theme: Theme = DefaultTheme
    assertNativeComponent(result,
      <.View(^.rnStyle := styles.statsContainer)(
        <.View(^.rnStyle := styles.statsProgress)(
          <.Text(^.rnStyle := themeTextStyle)(s"${props.progress}")
        )
      )
    )
  }
}
