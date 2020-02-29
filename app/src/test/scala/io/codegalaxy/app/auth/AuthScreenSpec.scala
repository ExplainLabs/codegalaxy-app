package io.codegalaxy.app.auth

import io.codegalaxy.app.auth.AuthActions.AuthAuthenticateAction
import io.codegalaxy.app.auth.AuthScreen._
import org.scalatest.{Assertion, Succeeded}
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils
import scommons.reactnative.TextInput._
import scommons.reactnative._

import scala.concurrent.Future
import scala.scalajs.js

class AuthScreenSpec extends AsyncTestSpec with ShallowRendererUtils {

  it should "dispatch actions but not call onSuccessfulLogin when non-successful login" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[AuthActions]
    val onSuccessfulLogin = mockFunction[Unit]
    val props = AuthScreenProps(dispatch, actions, onSuccessfulLogin = onSuccessfulLogin)
    val renderer = createRenderer()
    renderer.render(<(AuthScreen())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.getRenderOutput(), raw.TextInput)
    val emailText = "test@test.com"
    val passwordText = "test12345"
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)
    val List(button) = findComponents(renderer.getRenderOutput(), raw.TouchableOpacity)

    val authAction = AuthAuthenticateAction(
      FutureTask("Authenticate", Future.failed(new Exception("some error")))
    )
    (actions.authenticate _).expects(dispatch, emailText, passwordText).returning(authAction)

    //then
    dispatch.expects(authAction)
    onSuccessfulLogin.expects().never()

    //when
    button.props.onPress()

    authAction.task.future.failed.map(_ => Succeeded)
  }

  it should "dispatch actions and call onSuccessfulLogin when successful login" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[AuthActions]
    val onSuccessfulLogin = mockFunction[Unit]
    val props = AuthScreenProps(dispatch, actions, onSuccessfulLogin = onSuccessfulLogin)
    val renderer = createRenderer()
    renderer.render(<(AuthScreen())(^.wrapped := props)())
    val List(email, password) = findComponents(renderer.getRenderOutput(), raw.TextInput)
    val emailText = "test@test.com"
    val passwordText = "test12345"
    email.props.onChangeText(emailText)
    password.props.onChangeText(passwordText)
    val List(button) = findComponents(renderer.getRenderOutput(), raw.TouchableOpacity)

    val authAction = AuthAuthenticateAction(
      FutureTask("Authenticate", Future.successful(""))
    )
    (actions.authenticate _).expects(dispatch, emailText, passwordText).returning(authAction)

    //then
    dispatch.expects(authAction)
    onSuccessfulLogin.expects()

    //when
    button.props.onPress()

    authAction.task.future.map(_ => Succeeded)
  }

  it should "enable Login button when both fields are set" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[AuthActions]
    val props = AuthScreenProps(dispatch, actions, onSuccessfulLogin = () => ())
    val renderer = createRenderer()
    renderer.render(<(AuthScreen())(^.wrapped := props)())
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
    val dispatch = mockFunction[Any, Any]
    val actions = mock[AuthActions]
    val props = AuthScreenProps(dispatch, actions, onSuccessfulLogin = () => ())

    //when
    val result = shallowRender(<(AuthScreen())(^.wrapped := props)())

    //then
    assertLoginScreen(result, emailText = "", passwordText = "", disabled = true)
  }

  private def assertLoginScreen(result: ShallowInstance,
                                emailText: String,
                                passwordText: String,
                                disabled: Boolean): Assertion = {

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
