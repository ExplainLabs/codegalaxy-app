package io.codegalaxy.app.auth

import io.codegalaxy.app.auth.LoginScreen._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils
import scommons.reactnative.TextInput._
import scommons.reactnative._

import scala.scalajs.js

class LoginScreenSpec extends TestSpec with ShallowRendererUtils {

  it should "call onLogin when Login button is pressed" in {
    //given
    val onLogin = mockFunction[String, String, Unit]
    val props = LoginScreenProps(onLogin = onLogin)
    val renderer = createRenderer()
    renderer.render(<(LoginScreen())(^.wrapped := props)())
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
    val props = LoginScreenProps(onLogin = (_, _) => ())
    val renderer = createRenderer()
    renderer.render(<(LoginScreen())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.getRenderOutput(), raw.TextInput)
    val emailText = "test@test.com"
    val passwordText = "test12345"

    //when
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)

    //then
    assertLoginScreen(renderer.getRenderOutput(), emailText, passwordText, disabled = false)
  }

  it should "render component with disabled Login button" in {
    //given
    val props = LoginScreenProps(onLogin = (_, _) => ())

    //when
    val result = shallowRender(<(LoginScreen())(^.wrapped := props)())

    //then
    assertLoginScreen(result, emailText = "", passwordText = "", disabled = true)
  }

  private def assertLoginScreen(
    result: ShallowInstance,
    emailText: String,
    passwordText: String,
    disabled: Boolean): Unit = {

    assertNativeComponent(result, <.View(^.rnStyle := styles.container)(), {
      case List(text, email, password, button) =>
        assertNativeComponent(text, <.Text(^.rnStyle := styles.heading)(
          "Welcome to CodeGalaxy"
        ))
        assertNativeComponent(email, <.TextInput(
          ^.placeholder := "E-MAIL-ADDRESS",
          ^.rnStyle := styles.input,
          ^.keyboardType := KeyboardType.`email-address`,
          ^.value := emailText
        )())
        assertNativeComponent(password, <.TextInput(
          ^.placeholder := "PASSWORD",
          ^.rnStyle := styles.input,
          ^.secureTextEntry := true,
          ^.value := passwordText
        )())
        assertNativeComponent(button, <.TouchableOpacity(
          ^.disabled := disabled
        )(
            <.View(^.rnStyle := styles.button)(
              <.Text(^.rnStyle := js.Array(
                styles.buttonText,
                if (disabled) styles.buttonTextDisabled
                else styles.buttonTextEnabled
              ))("Login")
            )
          ))
    })
  }
}
