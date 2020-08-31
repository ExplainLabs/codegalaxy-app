package io.codegalaxy.app

import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.user.UserActions.UserLoginAction
import io.codegalaxy.app.user._
import io.codegalaxy.domain.ProfileEntity
import org.scalatest.Assertion
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.react.navigation.tab.TabBarOptions.LabelPosition
import scommons.react.navigation.tab._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative._

import scala.concurrent.Future
import scala.scalajs.js

class CodeGalaxyRootSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "render initial component" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(None), onAppReady = () => ())

    //when
    val result = shallowRender(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    assertNativeComponent(result, <.>()())
  }
  
  it should "dispatch actions and render LoginScreen if not logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val onAppReady = mockFunction[Unit]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(None), onAppReady = onAppReady)

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful(None)))
    val fetchTopicsAction = TopicsFetchAction(FutureTask("Fetching Topics", Future.successful(Nil)))

    //then
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    (actions.fetchTopics _).expects(dispatch, false).returning(fetchTopicsAction)
    dispatch.expects(profileAction)
    dispatch.expects(fetchTopicsAction)
    onAppReady.expects()

    //when
    val renderer = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    import codeGalaxyRoot._

    val resultF = for {
      _ <- profileAction.task.future
      _ <- fetchTopicsAction.task.future
    } yield ()

    resultF.map { _ =>
      TestRenderer.act { () =>
        renderer.update(<(codeGalaxyRoot())(^.wrapped := props)())
      }

      val result = renderer.root.children(0)
      assertNativeComponent(result, <.NavigationContainer()(), { children: List[TestInstance] =>
        val List(navigator) = children
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
    }
  }
  
  it should "dispatch actions and render main screen if logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val onAppReady = mockFunction[Unit]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val profile = Some(mock[ProfileEntity])
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(profile), onAppReady = onAppReady)

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful(profile)))
    val fetchTopicsAction = TopicsFetchAction(FutureTask("Fetching Topics", Future.successful(Nil)))

    //then
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    (actions.fetchTopics _).expects(dispatch, false).returning(fetchTopicsAction)
    dispatch.expects(profileAction)
    dispatch.expects(fetchTopicsAction)
    onAppReady.expects()

    //when
    val renderer = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    val resultF = for {
      _ <- profileAction.task.future
      _ <- fetchTopicsAction.task.future
    } yield ()

    resultF.map { _ =>
      TestRenderer.act { () =>
        renderer.update(<(codeGalaxyRoot())(^.wrapped := props)())
      }

      val result = renderer.root.children(0)
      assertCodeGalaxyRoot(result, codeGalaxyRoot)
    }
  }

  private def assertCodeGalaxyRoot(result: TestInstance, codeGalaxyRoot: CodeGalaxyRoot): Assertion = {
    import codeGalaxyRoot._

    def renderIcon(tab: TestInstance, size: Int, color: String): TestInstance = {
      val iconComp = tab.props.options.tabBarIcon(js.Dynamic.literal("size" -> size, "color" -> color))

      val wrapper = new FunctionComponent[Unit] {
        protected def render(props: Props): ReactElement = {
          iconComp.asInstanceOf[ReactElement]
        }
      }

      testRender(<(wrapper()).empty)
    }

    assertNativeComponent(result, <.NavigationContainer()(), { children: List[TestInstance] =>
      val List(navigator) = children
      assertNativeComponent(navigator,
        <(Tab.Navigator)(
          ^.initialRouteName := "Quizzes",
          ^.tabBarOptions := new TabBarOptions {
            override val labelPosition = LabelPosition.`below-icon`
          }
        )()
        , { children: List[TestInstance] =>
          val List(tab1, tab2) = children
          
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
