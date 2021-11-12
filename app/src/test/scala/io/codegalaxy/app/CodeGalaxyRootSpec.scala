package io.codegalaxy.app

import io.codegalaxy.app.chapter.ChapterState
import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import io.codegalaxy.app.topic.TopicState
import io.codegalaxy.app.user.UserActions.UserLoginAction
import io.codegalaxy.app.user._
import io.codegalaxy.domain._
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

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful((None, None))))

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
      val List(statusBar, safeAreaProv) = renderer.root.children.toList
      assertNativeComponent(statusBar, <.StatusBar(^.barStyle := StatusBar.BarStyle.`dark-content`)())
      assertNativeComponent(safeAreaProv, <.SafeAreaProvider()(), { children: List[TestInstance] =>
        val List(navContainer) = children
        assertNativeComponent(navContainer, <.NavigationContainer(^.theme := DefaultTheme)(), { children: List[TestInstance] =>
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
    val config = ConfigEntity(123, darkTheme = true)
    (state.userState _).expects()
      .returning(UserState(profile, Some(config)))
      .twice()

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful((profile, Some(config)))))
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
    eventually {
      import codeGalaxyRoot._

      val List(statusBar, safeAreaProv) = renderer.root.children.toList
      assertNativeComponent(statusBar, <.StatusBar(^.barStyle := StatusBar.BarStyle.`light-content`)())
      assertNativeComponent(safeAreaProv, <.SafeAreaProvider()(), { children: List[TestInstance] =>
        val List(navContainer) = children
        assertNativeComponent(navContainer, <.NavigationContainer(^.theme := DarkTheme)(), { children: List[TestInstance] =>
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

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful((None, None))))
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    (actions.fetchTopics _).expects(*, *).never()
    dispatch.expects(profileAction)
    onAppReady.expects()

    val renderer = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)())
    import codeGalaxyRoot._
    val prepareF = eventually {
      val result = renderer.root.children(1)
      findComponents(result, AppStack.Navigator) should not be empty
    }

    prepareF.map { _ =>
      val result = renderer.root.children(1)
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
      (state.topicState _).expects().returning(TopicState(Seq(Topic(
        entity = TopicEntity(
          id = 1,
          alias = topic,
          name = "Test Topic",
          lang = "en",
          numQuestions = 1,
          numPaid = 2,
          numLearners = 3,
          numChapters = 4,
          numTheory = Some(5),
          svgIcon = Some("test svg")
        ),
        stats = None
      ))))
      (state.chapterState _).expects().returning(ChapterState(Some(topic), Seq(Chapter(
        entity = ChapterEntity(
          id = 1,
          topic = topic,
          alias = chapter,
          name = "Test Chapter",
          numQuestions = 1,
          numPaid = 2,
          numLearners = 3,
          numChapters = 4,
          numTheory = Some(5)
        ),
        stats = Some(ChapterStats(
          id = 1,
          progress = 10,
          progressOnce = 20,
          progressAll = 100,
          freePercent = 30,
          paid = 40
        ))
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
    (state.userState _).expects().returning(UserState(None)).twice()

    val profileAction = UserLoginAction(FutureTask("Fetching Profile", Future.successful((None, None))))
    (actions.userProfileFetch _).expects(dispatch).returning(profileAction)
    dispatch.expects(profileAction)

    //when
    val result = createTestRenderer(<(codeGalaxyRoot())(^.wrapped := props)()).root

    //then
    result.children.toList should be(empty)
    
    eventually {
      result.children.toList should not be empty
    }
  }

  it should "render home tab component" in {
    //give
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    //when
    val result = testRender(<(codeGalaxyRoot.homeTabComp)()())

    //then
    assertHomeTabComp(result, codeGalaxyRoot)
  }

  it should "render user stack component" in {
    //given
    val actions = mock[CodeGalaxyActions]
    val codeGalaxyRoot = new CodeGalaxyRoot(actions)

    //when
    val result = testRender(<(codeGalaxyRoot.userStackComp)()())

    //then
    import codeGalaxyRoot._

    assertNativeComponent(result,
      <(UserStack.Navigator)(
        ^.initialRouteName := "Profile",
        ^.screenOptions := new StackScreenOptions {
          override val headerShown = false
        }
      )(
        <(UserStack.Screen)(^.name := "Profile", ^.component := userController())()
      )
    )
  }

  private def assertHomeTabComp(result: TestInstance, codeGalaxyRoot: CodeGalaxyRoot): Assertion = {
    import codeGalaxyRoot._

    def renderIcon(tab: TestInstance, size: Int, color: String): TestInstance = {
      val iconComp = tab.props.options.tabBarIcon(js.Dynamic.literal("size" -> size, "color" -> color))
      createTestRenderer(iconComp.asInstanceOf[ReactElement]).root
    }

    assertNativeComponent(result, <(HomeTab.Navigator)(
      ^.initialRouteName := "Quizzes",
      ^.screenOptions := new TabScreenOptions {
        override val headerShown = false
        override val tabBarShowLabel = false
      }
    )(), inside(_) { case List(tab1, tab2) =>
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
