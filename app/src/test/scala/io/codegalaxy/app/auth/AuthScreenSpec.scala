package io.codegalaxy.app.auth

import io.codegalaxy.app.popup.LoadingPopup
import io.codegalaxy.app.user.{UserActions, UserState}
import scommons.react._
import scommons.react.test.dom.AsyncTestSpec
import scommons.react.test.util.ShallowRendererUtils

class AuthScreenSpec extends AsyncTestSpec with ShallowRendererUtils {

  it should "render component with LoadingPopup" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val props = AuthScreenProps(dispatch, actions, UserState(), onSuccessfulLogin = () => ())

    //when
    val result = shallowRender(<(AuthScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.>()(
        <(LoadingPopup()).empty
      )
    )
  }
}
