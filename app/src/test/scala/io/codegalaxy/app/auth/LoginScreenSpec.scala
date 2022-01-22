package io.codegalaxy.app.auth

import io.codegalaxy.app.auth.LoginScreen._
import scommons.react.test._
import scommons.reactnative.KeyboardAvoidingView._
import scommons.reactnative.ScrollView._
import scommons.reactnative.TextInput._
import scommons.reactnative._
import scommons.reactnative.test.PlatformMock

import scala.scalajs.js

class LoginScreenSpec extends TestSpec with TestRendererUtils {

  it should "call onLogin when Login button is pressed" in {
    //given
    val onLogin = mockFunction[String, String, Unit]
    val props = LoginScreenProps(onLogin = onLogin, onSignup = () => ())
    val renderer = createTestRenderer(<(LoginScreen())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.root, <.TextInput.reactClass)
    val emailText = "test@test.com"
    val passwordText = "test12345"
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)
    val List(button, _) = findComponents(renderer.root, <.TouchableOpacity.reactClass)

    //then
    onLogin.expects(*, *).onCall { (email, password) =>
      email shouldBe emailText
      password shouldBe passwordText
    }

    //when
    button.props.onPress()
  }

  it should "call onSignup when Signup link is pressed" in {
    //given
    val onSignup = mockFunction[Unit]
    val props = LoginScreenProps(onLogin = (_, _) => (), onSignup = onSignup)
    val renderer = createTestRenderer(<(LoginScreen())(^.wrapped := props)())
    val List(_, link) = findComponents(renderer.root, <.TouchableOpacity.reactClass)

    //then
    onSignup.expects()

    //when
    link.props.onPress()
  }

  it should "enable Login button when both fields are set" in {
    //given
    val props = LoginScreenProps(onLogin = (_, _) => (), onSignup = () => ())
    val renderer = createTestRenderer(<(LoginScreen())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.root, <.TextInput.reactClass)
    val emailText = "test@test.com"
    val passwordText = "test12345"

    //when
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)

    //then
    assertLoginScreen(renderer.root.children(0), emailText, passwordText, disabled = false)
  }

  it should "render component with disabled Login button" in {
    //given
    val props = LoginScreenProps(onLogin = (_, _) => (), onSignup = () => ())

    //when
    val result = testRender(<(LoginScreen())(^.wrapped := props)())

    //then
    assertLoginScreen(result, emailText = "", passwordText = "", disabled = true)
  }

  it should "render component on Android platform" in {
    //given
    PlatformMock.use(Platform.android)
    val props = LoginScreenProps(onLogin = (_, _) => (), onSignup = () => ())

    //when
    val result = testRender(<(LoginScreen())(^.wrapped := props)())

    //then
    assertLoginScreen(result, emailText = "", passwordText = "", disabled = true)
    
    //cleanup
    PlatformMock.use(Platform.ios)
  }

  private def assertLoginScreen(result: TestInstance,
                                emailText: String,
                                passwordText: String,
                                disabled: Boolean): Unit = {

    assertNativeComponent(result,
      <.KeyboardAvoidingView(
        ^.rnStyle := styles.container,
        ^.behavior := {
          if (Platform.OS == Platform.ios) Behavior.padding
          else Behavior.height
        }
      )(
        <.ScrollView(
          ^.keyboardDismissMode := KeyboardDismissMode.`on-drag`,
          ^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.handled
        )(
          <.Text(^.rnStyle := styles.heading)(
            "Welcome to CodeGalaxy"
          ),
  
          <.TextInput(
            ^.placeholder := "E-MAIL-ADDRESS",
            ^.rnStyle := styles.input,
            ^.keyboardType := KeyboardType.`email-address`,
            ^.autoCapitalize := AutoCapitalize.none,
            ^.autoCompleteType := AutoCompleteType.off, // android
            ^.autoCorrect := false,
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
          ),

          <.TouchableOpacity(^.rnStyle := styles.signupButton)(
            <.Text(^.rnStyle := styles.signupText)(
              "Sign Up"
            )
          )
        )
      )
    )
  }
}
