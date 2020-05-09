package io.codegalaxy.app

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.CodeGalaxyRoot._
import io.codegalaxy.app.auth._
import io.codegalaxy.app.user.UserActions.UserProfileFetchAction
import io.codegalaxy.app.user.{UserActions, UserState}
import org.scalatest.{Assertion, Succeeded}
import scommons.react._
import scommons.react.navigation._
import scommons.react.navigation.tab.TabBarOptions.LabelPosition
import scommons.react.navigation.tab._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative._

import scala.concurrent.Future
import scala.scalajs.js

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
      assertCodeGalaxyRoot(mainScreen)
    })
  }

  private def assertCodeGalaxyRoot(result: ShallowInstance): Assertion = {

    def renderIcon(tab: ShallowInstance, size: Int, color: String): ShallowInstance = {
      val iconComp = tab.props.options.tabBarIcon(js.Dynamic.literal("size" -> size, "color" -> color))

      val wrapper = new FunctionComponent[Unit] {
        protected def render(props: Props): ReactElement = {
          iconComp.asInstanceOf[ReactElement]
        }
      }

      shallowRender(<(wrapper()).empty)
    }

    assertNativeComponent(result, <.NavigationContainer()(), { case List(navigator) =>
      assertNativeComponent(navigator,
        <(Tab.Navigator)(
          ^.initialRouteName := "Courses",
          ^.tabBarOptions := new TabBarOptions {
            override val labelPosition = LabelPosition.`below-icon`
          }
        )()
        , { case List(tab1, tab2) =>
          assertNativeComponent(tab1,
            <(Tab.Screen)(^.name := "Courses", ^.component := emptyComp)()
          )
          assertNativeComponent(renderIcon(tab1, 16, "green"),
            <(CodeGalaxyIcons.FontAwesome5)(^.name := "list", ^.rnSize := 16, ^.color := "green")()
          )

          assertNativeComponent(tab2,
            <(Tab.Screen)(^.name := "Me", ^.component := emptyComp)()
          )
          assertNativeComponent(renderIcon(tab2, 32, "red"),
            <(CodeGalaxyIcons.FontAwesome5)(^.name := "user", ^.rnSize := 32, ^.color := "red")()
          )
        })
    })
  }
}
