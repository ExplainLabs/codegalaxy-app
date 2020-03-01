package io.codegalaxy.app.popup

import io.codegalaxy.app.popup.LoginPopup._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils
import scommons.reactnative.Modal._
import scommons.reactnative.TextInput._
import scommons.reactnative._

import scala.scalajs.js

class LoginPopupSpec extends TestSpec with ShallowRendererUtils {

  it should "call onLogin when Login button is pressed" in {
    //given
    val onLogin = mockFunction[String, String, Unit]
    val props = LoginPopupProps(onLogin = onLogin)
    val renderer = createRenderer()
    renderer.render(<(LoginPopup())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.getRenderOutput(), raw.TextInput)
    val emailText = "test@test.com"
    val passwordText = "test12345"
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)
    val List(button) = findComponents(renderer.getRenderOutput(), raw.TouchableOpacity)

    //then
    onLogin.expects(emailText, passwordText)

    //when
    button.props.onPress()
  }

  it should "enable Login button when both fields are set" in {
    //given
    val props = LoginPopupProps(onLogin = (_, _) => ())
    val renderer = createRenderer()
    renderer.render(<(LoginPopup())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.getRenderOutput(), raw.TextInput)
    val emailText = "test@test.com"
    val passwordText = "test12345"

    //when
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)

    //then
    assertLoginPopup(renderer.getRenderOutput(), emailText, passwordText, disabled = false)
  }

  it should "render component with disabled Login button" in {
    //given
    val props = LoginPopupProps(onLogin = (_, _) => ())

    //when
    val result = shallowRender(<(LoginPopup())(^.wrapped := props)())

    //then
    assertLoginPopup(result, emailText = "", passwordText = "", disabled = true)
  }

  private def assertLoginPopup(result: ShallowInstance,
                               emailText: String,
                               passwordText: String,
                               disabled: Boolean): Unit = {

    assertNativeComponent(result,
      <.Modal(
        ^.animationType := AnimationType.slide,
        ^.transparent := false,
        ^.visible := true
      )(
        <.View(^.rnStyle := styles.container)(
          <.Text(^.rnStyle := styles.heading)(
            "Welcome to CodeGalaxy"
          ),

          <.TextInput(
            ^.placeholder := "E-MAIL-ADDRESS",
            ^.rnStyle := styles.input,
            ^.keyboardType := KeyboardType.`email-address`,
            ^.value := emailText
          )(),

          <.TextInput(
            ^.placeholder := "PASSWORD",
            ^.rnStyle := styles.input,
            ^.secureTextEntry := true,
            ^.value := passwordText
          )(),

          <.TouchableOpacity(
            ^.disabled := disabled
          )(
            <.View(^.rnStyle := styles.button)(
              <.Text(^.rnStyle := js.Array(
                styles.buttonText,
                if (disabled) styles.buttonTextDisabled
                else styles.buttonTextEnabled
              ))("Login")
            )
          )
        )
      )
    )
  }
}
