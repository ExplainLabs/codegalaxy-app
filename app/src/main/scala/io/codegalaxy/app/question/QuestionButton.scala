package io.codegalaxy.app.question

import scommons.expo.VectorIcons
import scommons.react._
import scommons.reactnative._

import scala.scalajs.js

case class QuestionButtonProps(text: String, onPress: js.Function0[Unit])

object QuestionButton extends FunctionComponent[QuestionButtonProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped

    <.TouchableOpacity(
      ^.rnStyle := styles.button,
      ^.onPress := props.onPress
    )(
      <.Text(^.rnStyle := styles.buttonText)(props.text),
      <(VectorIcons.Ionicons)(
        ^.name := "ios-arrow-forward",
        ^.rnSize := 24,
        ^.color := Style.Color.dodgerblue
      )()
    )
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import TextStyle._
    import ViewStyle._

    val button: Style = new ViewStyle {
      override val alignSelf = AlignSelf.center
      override val flexDirection = FlexDirection.row
      override val alignItems = AlignItems.center
      override val marginVertical = 10
    }
    val buttonText: Style = new TextStyle {
      override val fontWeight = FontWeight.bold
      override val fontSize = 18
      override val color = Style.Color.dodgerblue
      override val marginRight = 5
    }
  }
}
