package io.codegalaxy.app

import io.codegalaxy.app.user.{UserActions, UserState}
import scommons.react._
import scommons.react.test.dom.AsyncTestSpec
import scommons.react.test.util.ShallowRendererUtils

class CodeGalaxyRootSpec extends AsyncTestSpec with ShallowRendererUtils {

  it should "render empty component" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(), onAppReady = () => (), onSuccessfulLogin = () => ())

    //when
    val result = shallowRender(<(CodeGalaxyRoot())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.>()()
    )
  }
}
