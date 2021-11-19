package io.codegalaxy.app.question

import io.codegalaxy.app.question.QuestionAnswer.styles
import scommons.react.test._
import scommons.reactnative._

class QuestionAnswerSpec extends TestSpec with TestRendererUtils {

  it should "render component when correct answer" in {
    //given
    val props = QuestionAnswerProps(correct = true)
    
    //when
    val result = testRender(<(QuestionAnswer())(^.wrapped := props)())
    
    //then
    assertNativeComponent(result,
      <.Text(^.rnStyle := styles.rightAnswer)("Well done! Right answer.")
    )
  }

  it should "render component when incorrect answer" in {
    //given
    val props = QuestionAnswerProps(correct = false)
    
    //when
    val result = testRender(<(QuestionAnswer())(^.wrapped := props)())
    
    //then
    assertNativeComponent(result,
      <.Text(^.rnStyle := styles.wrongAnswer)("Oops! This is the wrong answer.")
    )
  }
}
