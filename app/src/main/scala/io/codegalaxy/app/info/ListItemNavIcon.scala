package io.codegalaxy.app.info

import scommons.react._
import scommons.reactnative._
import scommons.reactnative.svg._

import scala.scalajs.js

case class ListItemNavIconProps(progress: Int,
                                showLabel: Boolean)

object ListItemNavIcon extends FunctionComponent[ListItemNavIconProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
    <.View(^.rnStyle := styles.statsContainer)(
      if (props.progress > 0) <.>()(
        if (props.showLabel) Some(<.Text(^.rnStyle := styles.statsLabel)("Open"))
        else None,
        <.View(^.rnStyle := styles.statsProgress)(
          <.Text()(s"${props.progress}")
        )
      )
      else <.>()(
        if (props.showLabel) Some(<.Text(^.rnStyle := styles.statsLabel)("Start"))
        else None,
        <.SvgXml(^.rnStyle := styles.startSvg, ^.xml := startSvgXml)()
      )
    )
  }

  private[info] lazy val startSvgXml: String =
    """<svg width="32" height="32" viewBox="0 0 512 512">
      |  <path
      |    fill="#1e90ff"
      |    d="M256 504c137 0 248-111 248-248S393 8 256 8 8 119 8 256s111 248 248 248zM40 256c0-118.7 96.1-216 216-216 118.7 0 216 96.1 216 216 0 118.7-96.1 216-216 216-118.7 0-216-96.1-216-216zm331.7-18l-176-107c-15.8-8.8-35.7 2.5-35.7 21v208c0 18.4 19.8 29.8 35.7 21l176-101c16.4-9.1 16.4-32.8 0-42zM192 335.8V176.9c0-4.7 5.1-7.6 9.1-5.1l134.5 81.7c3.9 2.4 3.8 8.1-.1 10.3L201 341c-4 2.3-9-.6-9-5.2z"
      |  />
      |</svg>
      |""".stripMargin

  private[info] lazy val styles = StyleSheet.create(new Styles)
  private[info] class Styles extends js.Object {
    import Style._
    import TextStyle._
    import ViewStyle._

    val statsContainer: Style = new ViewStyle {
      override val flex = 1
      override val flexDirection = FlexDirection.row
      override val flexWrap = FlexWrap.wrap
      override val alignItems = AlignItems.center
      override val justifyContent = JustifyContent.`flex-end`
    }
    val statsLabel: Style = new TextStyle {
      override val color = Color.dodgerblue
      override val fontWeight = FontWeight.bold
      override val marginRight = 5
    }
    val statsProgress: Style = new ViewStyle {
      override val alignItems = AlignItems.center
      override val justifyContent = JustifyContent.center
      override val width = 32
      override val height = 32
      override val borderRadius = 16
      override val borderWidth = 2
      override val borderColor = Color.dodgerblue
    }
    val startSvg: Style = new ViewStyle {
      override val color = Color.dodgerblue
      override val width = "100%"
      override val height = "100%"
    }
  }
}
