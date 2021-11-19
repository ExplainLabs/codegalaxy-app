package io.codegalaxy.app.question

import io.codegalaxy.app.question.QuestionRule._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative._

import scala.scalajs.js

class QuestionRuleSpec extends TestSpec with TestRendererUtils {

  QuestionRule.questionTextComp = mockUiComponent("QuestionText")

  implicit val theme: Theme = DefaultTheme

  it should "render component" in {
    //given
    val props = QuestionRuleProps("test title", "test text")
    
    //when
    val root = createTestRenderer(<(QuestionRule())(^.wrapped := props)()).root
    
    //then
    inside(root.children.toList) { case List(title, text) =>
      assertNativeComponent(title,
        <.Text(themeStyle(styles.ruleTitle, themeTextStyle))(props.title)
      )
      assertNativeComponent(text,
        <(questionTextComp())(^.wrapped := QuestionTextProps(
          textHtml = props.text,
          style = Some(js.Array(styles.ruleText))
        ))()
      )
    }
  }
}
