package io.codegalaxy.app.question

import scommons.expo.VectorIcons
import scommons.react.test._
import scommons.reactnative._

class QuestionAnswerIconSpec extends TestSpec with TestRendererUtils {

  it should "render component when correct answer" in {
    //given
    val props = QuestionAnswerIconProps(correct = true)
    
    //when
    val result = testRender(<(QuestionAnswerIcon())(^.wrapped := props)())
    
    //then
    assertNativeComponent(result,
      <(VectorIcons.Ionicons)(
        ^.name := "ios-checkmark",
        ^.rnSize := 24,
        ^.color := Style.Color.green
      )()
    )
  }

  it should "render component when incorrect answer" in {
    //given
    val props = QuestionAnswerIconProps(correct = false)
    
    //when
    val result = testRender(<(QuestionAnswerIcon())(^.wrapped := props)())
    
    //then
    assertNativeComponent(result,
      <(VectorIcons.Ionicons)(
        ^.name := "ios-close",
        ^.rnSize := 24,
        ^.color := Style.Color.red
      )()
    )
  }
}
