package io.codegalaxy.app.auth

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.user.UserActions
import io.codegalaxy.app.user.UserActions._
import org.scalamock.function.MockFunction1
import scommons.react.navigation.Navigation
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future

class LoginControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[UserActions]
    val controller = new LoginController(actions)
    
    //when & then
    controller.uiComponent shouldBe LoginScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val controller = new LoginController(actions)
    val state = mock[CodeGalaxyStateDef]
    val nav = mock[Navigation]

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
                          actions: UserActions,
                          onLogin: (String, String) => Unit): Unit = {
    //given
    val email = "test_email"
    val password = "test_password"
    val action = UserLoginAction(
      FutureTask("Logging-in User", Future.successful((None, None)))
    )
    (actions.userLogin _).expects(dispatch, email, password).returning(action)

    //then
    dispatch.expects(action)

    //when
    onLogin(email, password)
  }

  private def assertSignup(dispatch: MockFunction1[Any, Any],
                           actions: UserActions,
                           onSignup: () => Unit): Unit = {
    //given
    val action = UserSignupAction(
      FutureTask("Signing up User", Future.unit)
    )
    (actions.userSignup _).expects().returning(action)

    //then
    dispatch.expects(action)

    //when
    onSignup()
  }
}
