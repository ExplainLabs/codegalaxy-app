package io.codegalaxy.app.question

import scommons.expo.VectorIcons
import scommons.react._
import scommons.reactnative._

case class QuestionAnswerIconProps(correct: Boolean)

object QuestionAnswerIcon extends FunctionComponent[QuestionAnswerIconProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped

    <(VectorIcons.Ionicons)(
      ^.name := {
        if (props.correct) "ios-checkmark"
        else "ios-close"
      },
      ^.rnSize := 24,
      ^.color := {
        if (props.correct) Style.Color.green
        else Style.Color.red
      }
    )()
  }
}
