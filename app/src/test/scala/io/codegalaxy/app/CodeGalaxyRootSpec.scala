package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.user._
import org.scalatest.Assertion
import scommons.react._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.react.navigation.tab.TabBarOptions.LabelPosition
import scommons.react.navigation.tab._
import scommons.react.test._
import scommons.reactnative._

import scala.scalajs.js

class CodeGalaxyRootSpec extends TestSpec with ShallowRendererUtils {

  it should "render LoginScreen if not logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    val props = CodeGalaxyRootProps(dispatch, actions, UserState(None), onAppReady = () => ())

    //when
    val result = shallowRender(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    assertComponent(result, WithAutoLogin)({ case WithAutoLoginProps(resDispatch, resActions, onReady) =>
      resDispatch shouldBe dispatch
      resActions shouldBe actions
      onReady shouldBe props.onAppReady
    }, { case List(navContainer) =>
      import codeGalaxyRoot._

      assertNativeComponent(navContainer, <.NavigationContainer()(), { case List(navigator) =>
        assertNativeComponent(navigator,
          <(Stack.Navigator)(
            ^.screenOptions := new StackScreenOptions {
              override val headerShown = false
            }
          )(
            <(Stack.Screen)(^.name := "Login", ^.component := loginController())()
          )
        )
      })
    })
  }
  
  it should "render main screen if logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    val loginData = Some(mock[UserLoginState])
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(loginData), onAppReady = () => ())

    //when
    val result = shallowRender(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    assertComponent(result, WithAutoLogin)({ case WithAutoLoginProps(resDispatch, resActions, onReady) =>
      resDispatch shouldBe dispatch
      resActions shouldBe actions
      onReady shouldBe props.onAppReady
    }, { case List(mainScreen) =>
      assertCodeGalaxyRoot(mainScreen, codeGalaxyRoot)
    })
  }

  private def assertCodeGalaxyRoot(result: ShallowInstance, codeGalaxyRoot: CodeGalaxyRoot): Assertion = {
    import codeGalaxyRoot._

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
          ^.initialRouteName := "Quizzes",
          ^.tabBarOptions := new TabBarOptions {
            override val labelPosition = LabelPosition.`below-icon`
          }
        )()
        , { case List(tab1, tab2) =>
          assertNativeComponent(tab1,
            <(Tab.Screen)(^.name := "Quizzes", ^.component := topicStackComp)()
          )
          assertNativeComponent(renderIcon(tab1, 16, "green"),
            <(CodeGalaxyIcons.FontAwesome5)(^.name := "list", ^.rnSize := 16, ^.color := "green")()
          )

          assertNativeComponent(tab2,
            <(Tab.Screen)(^.name := "Me", ^.component := userStackComp)()
          )
          assertNativeComponent(renderIcon(tab2, 32, "red"),
            <(CodeGalaxyIcons.FontAwesome5)(^.name := "user", ^.rnSize := 32, ^.color := "red")()
          )
        })
    })
  }
}
