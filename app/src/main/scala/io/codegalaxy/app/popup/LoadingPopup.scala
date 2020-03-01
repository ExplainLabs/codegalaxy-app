package io.codegalaxy.app.popup

import io.codegalaxy.app.CodeGalaxyTheme
import scommons.react._
import scommons.reactnative.Modal._
import scommons.reactnative._

import scala.scalajs.js

object LoadingPopup extends FunctionComponent[Unit] {

  protected def render(compProps: Props): ReactElement = {
    <.Modal(
      ^.animationType := AnimationType.none,
      ^.transparent := false,
      ^.visible := true
    )(
      <.View(^.rnStyle := styles.loading)(
        <.Text()("Loading...")
      )
    )
  }

  private[popup] lazy val styles = StyleSheet.create(new Styles)
  private[popup] class Styles extends js.Object {
    import ViewStyle._

    val loading: Style = new ViewStyle {
      override val backgroundColor = CodeGalaxyTheme.Colors.primary
      override val flex = 1
      override val justifyContent = JustifyContent.center
      override val alignItems = AlignItems.center
    }
  }
}
