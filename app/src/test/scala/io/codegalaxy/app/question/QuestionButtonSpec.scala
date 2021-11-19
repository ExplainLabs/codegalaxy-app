package io.codegalaxy.app.question

import io.codegalaxy.app.question.QuestionButton.styles
import scommons.expo.VectorIcons
import scommons.react.test._
import scommons.reactnative._

class QuestionButtonSpec extends TestSpec with TestRendererUtils {

  it should "render component" in {
    //given
    val props = QuestionButtonProps("test text", onPress = () => ())
    
    //when
    val result = testRender(<(QuestionButton())(^.wrapped := props)())
    
    //then
    assertNativeComponent(result,
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
    )
  }
}
