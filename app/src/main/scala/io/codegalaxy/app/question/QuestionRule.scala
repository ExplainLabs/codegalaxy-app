package io.codegalaxy.app.question

import scommons.react._
import scommons.react.navigation._
import scommons.reactnative._

import scala.scalajs.js

case class QuestionRuleProps(title: String, text: String)

object QuestionRule extends FunctionComponent[QuestionRuleProps] {

  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText

  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val props = compProps.wrapped

    <.>()(
      <.Text(themeStyle(styles.ruleTitle, themeTextStyle))(props.title),

      <(questionTextComp())(^.wrapped := QuestionTextProps(
        textHtml = props.text,
        style = Some(js.Array(styles.ruleText))
      ))()
    )
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import TextStyle._

    val ruleTitle: Style = new TextStyle {
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val marginVertical = 5
    }
    val ruleText: Style = new ViewStyle {
      override val marginVertical = 5
    }
  }
}
