package io.codegalaxy.app.popup

import io.codegalaxy.app.popup.LoadingPopup._
import scommons.react.test.TestSpec
import scommons.react.test.util.ShallowRendererUtils
import scommons.reactnative.Modal._
import scommons.reactnative._

class LoadingPopupSpec extends TestSpec with ShallowRendererUtils {

  it should "render component" in {
    //given
    val component = <(LoadingPopup())()()

    //when
    val result = shallowRender(component)

    //then
    assertNativeComponent(result,
      <.Modal(
        ^.animationType := AnimationType.none,
        ^.transparent := false,
        ^.visible := true
      )(
        <.View(^.rnStyle := styles.loading)(
          <.Text()("Loading...")
        )
      )
    )
  }
}
