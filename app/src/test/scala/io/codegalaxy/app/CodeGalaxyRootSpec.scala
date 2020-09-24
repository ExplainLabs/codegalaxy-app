package io.codegalaxy.app

import io.codegalaxy.app.chapter.ChapterState
import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.topic.TopicState
import io.codegalaxy.app.user.UserActions.UserLoginAction
import io.codegalaxy.app.user._
import io.codegalaxy.domain.{ChapterEntity, ProfileEntity, TopicEntity}
import org.scalatest.Assertion
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.react.navigation.tab._
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative._
import scommons.reactnative.safearea._

import scala.concurrent.Future
import scala.scalajs.js

class CodeGalaxyRootSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch actions and render LoginScreen if not logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val state = mock[CodeGalaxyStateDef]
    val onAppReady = mockFunction[Unit]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val props = CodeGalaxyRootProps(dispatch, actions, state, onAppReady = onAppReady)
    (state.userState _).expects().returning(UserState(None)).twice()

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful(None)))

    //then
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    (actions.fetchTopics _).expects(*, *).never()
    dispatch.expects(profileAction)
    onAppReady.expects()

    //when
    val renderer = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    import codeGalaxyRoot._

    eventually {
      val result = renderer.root.children(0)
      assertNativeComponent(result, <.SafeAreaProvider()(), { children: List[TestInstance] =>
        val List(navContainer) = children
        assertNativeComponent(navContainer, <.NavigationContainer()(), { children: List[TestInstance] =>
          val List(navigator) = children
          assertNativeComponent(navigator,
            <(LoginStack.Navigator)(
              ^.screenOptions := new StackScreenOptions {
                override val headerShown = false
              }
            )(
              <(LoginStack.Screen)(^.name := "Login", ^.component := loginController())()
            )
          )
        })
      })
    }
  }
  
  it should "dispatch actions and render main screen if logged-in" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val state = mock[CodeGalaxyStateDef]
    val onAppReady = mockFunction[Unit]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val profile = Some(mock[ProfileEntity])
    val props = CodeGalaxyRootProps(dispatch, actions, state, onAppReady = onAppReady)
    (state.userState _).expects().returning(UserState(profile)).twice()

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
      import codeGalaxyRoot._

      val result = renderer.root.children(0)
      assertNativeComponent(result, <.SafeAreaProvider()(), { children: List[TestInstance] =>
        val List(navContainer) = children
        assertNativeComponent(navContainer, <.NavigationContainer()(), { children: List[TestInstance] =>
          val List(navigator) = children
          assertNativeComponent(navigator,
            <(AppStack.Navigator)()(
              <(AppStack.Screen)(^.name := "Home", ^.component := homeTabComp)(),
              <(AppStack.Screen)(^.name := "Quiz", ^.component := chapterListController())(),
              <(AppStack.Screen)(^.name := "Question", ^.component := questionController())()
            )
          )
        })
      })
    }
  }

  it should "render dynamic app stack screens titles" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val state = mock[CodeGalaxyStateDef]
    val onAppReady = mockFunction[Unit]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val profile = Some(mock[ProfileEntity])
    val props = CodeGalaxyRootProps(dispatch, actions, state, onAppReady = onAppReady)
    (state.userState _).expects().returning(UserState(profile)).twice()

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful(None)))
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    (actions.fetchTopics _).expects(*, *).never()
    dispatch.expects(profileAction)
    onAppReady.expects()

    val renderer = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)())
    import codeGalaxyRoot._
    val prepareF = eventually {
      val result = renderer.root.children(0)
      findComponents(result, AppStack.Navigator) should not be empty
    }

    prepareF.map { _ =>
      val result = renderer.root.children(0)
      val List(appStackNav) = findComponents(result, AppStack.Navigator)
      val topic = "test_topic"
      val chapter = "test_chapter"

      def navProps(route: String): js.Dynamic = {
        js.Dynamic.literal(
          "navigation" -> js.Dynamic.literal(),
          "route" -> js.Dynamic.literal(
            "name" -> route,
            "params" -> js.Dynamic.literal(
              "topic" -> topic,
              "chapter" -> chapter
            )
          )
        )
      }

      //then
      (state.topicState _).expects().returning(TopicState(Seq(TopicEntity(
        id = 1,
        alias = topic,
        name = "Test Topic",
        lang = "en",
        numQuestions = 1,
        numPaid = 2,
        numLearners = 3,
        numChapters = 4,
        svgIcon = Some("test svg"),
        progress = None
      ))))
      (state.chapterState _).expects().returning(ChapterState(Some(topic), Seq(ChapterEntity(
        id = 1,
        topic = topic,
        alias = chapter,
        name = "Test Chapter",
        numQuestions = 1,
        numPaid = 2,
        numLearners = 3,
        numChapters = 4,
        progress = 5
      ))))

      //when & then
      inside(appStackNav.props.screenOptions(navProps("Quiz"))) { case opts =>
        opts.headerBackTitleVisible shouldBe false
        opts.title shouldBe "Test Topic"
      }
      //when & then
      inside(appStackNav.props.screenOptions(navProps("Question"))) { case opts =>
        opts.headerBackTitleVisible shouldBe false
        opts.title shouldBe "Test Chapter"
      }
      //when & then
      inside(appStackNav.props.screenOptions(navProps("Home"))) { case opts =>
        opts.headerBackTitleVisible shouldBe false
        opts.title shouldBe "Quizzes"
      }
      //when & then
      inside(appStackNav.props.screenOptions(navProps("otherRoute"))) { case opts =>
        opts.headerBackTitleVisible shouldBe false
        opts.title shouldBe "otherRoute"
      }
    }
  }

  it should "render initial empty component" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[CodeGalaxyActions]
    val state = mock[CodeGalaxyStateDef]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)
    val props = CodeGalaxyRootProps(dispatch, actions, state, onAppReady = () => ())
    (state.userState _).expects().returning(UserState(None))

    //when
    val result = shallowRender(<(codeGalaxyRoot())(^.wrapped := props)())

    //then
    assertNativeComponent(result, <.>()())
  }

  it should "render home tab component" in {
    //give
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    //when
    val result = shallowRender(<(codeGalaxyRoot.homeTabComp)()())

    //then
    assertHomeTabComp(result, codeGalaxyRoot)
  }

  it should "render user stack component" in {
    //given
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    //when
    val result = shallowRender(<(codeGalaxyRoot.userStackComp)()())

    //then
    import codeGalaxyRoot._

    assertNativeComponent(result,
      <(UserStack.Navigator)(^.initialRouteName := "Profile")(
        <(UserStack.Screen)(^.name := "Profile", ^.component := userController())()
      )
    )
  }

  private def assertHomeTabComp(result: ShallowInstance, codeGalaxyRoot: CodeGalaxyRoot): Assertion = {
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

    assertNativeComponent(result, <(HomeTab.Navigator)(
      ^.initialRouteName := "Quizzes",
      ^.tabBarOptions := new TabBarOptions {
        override val showLabel = false
      }
    )(), { children: List[ShallowInstance] =>
      val List(tab1, tab2) = children

      assertNativeComponent(tab1,
        <(HomeTab.Screen)(^.name := "Quizzes", ^.component := topicListController())()
      )
      assertNativeComponent(renderIcon(tab1, 16, "green"),
        <(CodeGalaxyIcons.FontAwesome5)(^.name := "list", ^.rnSize := 16, ^.color := "green")()
      )

      assertNativeComponent(tab2,
        <(HomeTab.Screen)(^.name := "Me", ^.component := userStackComp)()
      )
      assertNativeComponent(renderIcon(tab2, 32, "red"),
        <(CodeGalaxyIcons.FontAwesome5)(^.name := "user", ^.rnSize := 32, ^.color := "red")()
      )
    })
  }
}
