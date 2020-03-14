package io.codegalaxy.app

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.auth._
import io.codegalaxy.app.user.UserActions.UserProfileFetchAction
import io.codegalaxy.app.user.{UserActions, UserState}
import org.scalatest.Succeeded
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec
import scommons.react.test.util.ShallowRendererUtils
import scommons.reactnative._

import scala.concurrent.Future

class CodeGalaxyRootSpec extends TestSpec with ShallowRendererUtils {

  it should "dispatch action when onLogin" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    //not logged-in
    val userProfile = Option.empty[UserProfileData]
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(userProfile), onAppReady = () => ())
    val comp = shallowRender(<(CodeGalaxyRoot())(^.wrapped := props)())
    val loginProps = findComponentProps(comp, LoginScreen)
    val email = "test user"
    val password = "test password"

    val loginAction = UserProfileFetchAction(
      FutureTask("Logging-in User", Future.successful(userProfile))
    )
    (actions.userAuth _).expects(dispatch, email, password).returning(loginAction)

    //then
    dispatch.expects(loginAction)

    //when
    loginProps.onLogin(email, password)
  }
  
  it should "render LoginScreen if not logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val userProfile = Option.empty[UserProfileData]
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(userProfile), onAppReady = () => ())

    //when
    val result = shallowRender(<(CodeGalaxyRoot())(^.wrapped := props)())

    //then
    assertComponent(result, WithAutoLogin)({ case WithAutoLoginProps(resDispatch, resActions, onReady) =>
      resDispatch shouldBe dispatch
      resActions shouldBe actions
      onReady shouldBe props.onAppReady
    }, { case List(login) =>
      assertComponent(login, LoginScreen) { case LoginScreenProps(_) =>
        Succeeded
      }
    })
  }
  
  it should "render main screen if logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val userProfile = Some(mock[UserProfileData])
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(userProfile), onAppReady = () => ())

    //when
    val result = shallowRender(<(CodeGalaxyRoot())(^.wrapped := props)())

    //then
    assertComponent(result, WithAutoLogin)({ case WithAutoLoginProps(resDispatch, resActions, onReady) =>
      resDispatch shouldBe dispatch
      resActions shouldBe actions
      onReady shouldBe props.onAppReady
    }, { case List(mainScreen) =>
      assertNativeComponent(mainScreen,
        <.Text()("TODO: main screen")
      )
    })
  }
}
