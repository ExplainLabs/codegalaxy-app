package io.codegalaxy.app.auth

import io.codegalaxy.app.user.MockUserActions
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.{MockCodeGalaxyState, MockNavigation}
import org.scalamock.function.MockFunction1
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future

class LoginControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class Actions {
    val userLogin = mockFunction[Dispatch, String, String, UserLoginAction]
    val userSignup = mockFunction[UserSignupAction]

    val actions = new MockUserActions(
      userLoginMock = userLogin,
      userSignupMock = userSignup
    )
  }

  it should "return component" in {
    //given
    val actions = new Actions
    val controller = new LoginController(actions.actions)
    
    //when & then
    controller.uiComponent shouldBe LoginScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = new Actions
    val controller = new LoginController(actions.actions)
    val state = new MockCodeGalaxyState
    val nav = new MockNavigation

    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case LoginScreenProps(onLogin, onSignup) =>
        assertLogin(dispatch, actions, onLogin)
        assertSignup(dispatch, actions, onSignup)
    }
  }

  private def assertLogin(dispatch: MockFunction1[Any, Any],
                          actions: Actions,
                          onLogin: (String, String) => Unit): Unit = {
    //given
    val email = "test_email"
    val password = "test_password"
    val action = UserLoginAction(
      FutureTask("Logging-in User", Future.successful((None, None)))
    )
    actions.userLogin.expects(dispatch, *, *).onCall { (_, e, p) =>
      e shouldBe email
      p shouldBe password
      action
    }

    //then
    dispatch.expects(action)

    //when
    onLogin(email, password)
  }

  private def assertSignup(dispatch: MockFunction1[Any, Any],
                           actions: Actions,
                           onSignup: () => Unit): Unit = {
    //given
    val action = UserSignupAction(
      FutureTask("Signing up User", Future.unit)
    )
    actions.userSignup.expects().returning(action)

    //then
    dispatch.expects(action)

    //when
    onSignup()
  }
}
