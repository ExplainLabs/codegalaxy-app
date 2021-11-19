package io.codegalaxy.app.question

import scommons.react._
import scommons.reactnative._

import scala.scalajs.js

case class QuestionAnswerProps(correct: Boolean)

object QuestionAnswer extends FunctionComponent[QuestionAnswerProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped

    if (props.correct) {
      <.Text(^.rnStyle := styles.rightAnswer)("Well done! Right answer.")
    } else {
      <.Text(^.rnStyle := styles.wrongAnswer)("Oops! This is the wrong answer.")
    }
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import TextStyle._

    val rightAnswer: Style = new TextStyle {
      override val marginVertical = 5
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val color = Style.Color.green
      override val backgroundColor = Style.Color.lightcyan
    }
    val wrongAnswer: Style = new TextStyle {
      override val marginVertical = 5
      override val textAlign = TextAlign.center
      override val fontWeight = FontWeight.bold
      override val color = Style.Color.red
      override val backgroundColor = Style.Color.lightpink
    }
  }
}
