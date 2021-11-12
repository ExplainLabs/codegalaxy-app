package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.chapter.ChapterListController
import io.codegalaxy.app.question.QuestionController
import io.codegalaxy.app.topic.{TopicListController, TopicParams}
import io.codegalaxy.app.user._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.react.navigation.tab._
import scommons.reactnative._
import scommons.reactnative.safearea._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

case class CodeGalaxyRootProps(dispatch: Dispatch,
                               actions: UserActions,
                               state: CodeGalaxyStateDef,
                               onAppReady: () => Unit)

class CodeGalaxyRoot(actions: CodeGalaxyActions) extends FunctionComponent[CodeGalaxyRootProps] {

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val userState = props.state.userState
    val showLogin = userState.profile.isEmpty
    val (isReady, setIsReady) = useState(false)

    def getParams(navProps: NavigationProps): TopicParams = {
      val params = new Navigation(navProps.navigation, navProps.route).getParams
      TopicParams.fromMap(params)
    }
    
    def findTopicName(alias: String): Option[String] = {
      props.state.topicState.topics.find(_.entity.alias == alias).map(_.entity.name)
    }

    def findChapterName(alias: String): Option[String] = {
      props.state.chapterState.chapters.find(_.entity.alias == alias).map(_.entity.name)
    }
    
    def getScreenTitle(navProps: NavigationProps): String = {
      val routeName = getFocusedRouteNameFromRoute(navProps.route)
        .getOrElse(navProps.route.name)
      
      routeName match {
        case "Quiz" => findTopicName(getParams(navProps).topic).getOrElse(routeName)
        case "Question" => findChapterName(getParams(navProps).getChapter).getOrElse(routeName)
        case "Home" => "Quizzes"
        case _ => routeName
      }
    }

    useEffect({ () =>
      val initF = for {
        (maybeProfile, _) <- {
          val action = props.actions.userProfileFetch(props.dispatch)
          props.dispatch(action)
          action.task.future
        }
        _ <- if (maybeProfile.isDefined) {
          val action = actions.fetchTopics(props.dispatch)
          props.dispatch(action)
          action.task.future
        }
        else Future.successful(Nil)
      } yield ()

      initF.andThen { case _ =>
        setIsReady(true)
        props.onAppReady()
      }
      ()
    }, Nil)

    if (!isReady) <.>()() //Loading...
    else {
      <.>()(
        <.StatusBar(^.barStyle := {
          if (userState.config.exists(_.darkTheme)) StatusBar.BarStyle.`light-content`
          else StatusBar.BarStyle.`dark-content`
        })(),

        <.SafeAreaProvider()(
          <.NavigationContainer(^.theme := {
            if (userState.config.exists(_.darkTheme)) DarkTheme
            else DefaultTheme
          })(
            if (showLogin) {
              <(LoginStack.Navigator)(
                ^.screenOptions := new StackScreenOptions {
                  override val headerShown = false
                }
              )(
                <(LoginStack.Screen)(^.name := "Login", ^.component := loginController())()
              )
            }
            else {
              <(AppStack.Navigator)(
                ^.screenOptions := { navProps: NavigationProps =>
                  val screenTitle = getScreenTitle(navProps)
                  val options = new StackScreenOptions {
                    val headerBackTitleVisible = false
                    override val title = screenTitle
                  }
                  options
                }
              )(
                <(AppStack.Screen)(^.name := "Home", ^.component := homeTabComp)(),
                <(AppStack.Screen)(^.name := "Quiz", ^.component := chapterListController())(),
                <(AppStack.Screen)(^.name := "Question", ^.component := questionController())()
              )
            }
          )
        )
      )
    }
  }

  private[app] lazy val loginController = new LoginController(actions)
  private[app] lazy val topicListController = new TopicListController(actions)
  private[app] lazy val chapterListController = new ChapterListController(actions)
  private[app] lazy val questionController = new QuestionController(actions)
  private[app] lazy val userController = new UserController(actions)

  private[app] lazy val LoginStack = createStackNavigator()
  private[app] lazy val AppStack = createStackNavigator()

  private[app] lazy val HomeTab = createBottomTabNavigator()
  private[app] lazy val homeTabComp: ReactClass = new FunctionComponent[Unit] {
    protected def render(props: Props): ReactElement = {
      <(HomeTab.Navigator)(
        ^.initialRouteName := "Quizzes",
        ^.screenOptions := new TabScreenOptions {
          override val headerShown = false
          override val tabBarShowLabel = false
        }
      )(
        <(HomeTab.Screen)(
          ^.name := "Quizzes",
          ^.component := topicListController(),
          ^.options := new TabScreenOptions {
            override val tabBarIcon = { params =>
              <(CodeGalaxyIcons.FontAwesome5)(^.name := "list", ^.rnSize := params.size, ^.color := params.color)()
            }: js.Function1[TabBarIconParams, ReactElement]
          }
        )(),
        <(HomeTab.Screen)(
          ^.name := "Me",
          ^.component := userStackComp,
          ^.options := new TabScreenOptions {
            override val tabBarIcon = { params =>
              <(CodeGalaxyIcons.FontAwesome5)(^.name := "user", ^.rnSize := params.size, ^.color := params.color)()
            }: js.Function1[TabBarIconParams, ReactElement]
          }
        )()
      )
    }
  }.apply()
  
  private[app] lazy val UserStack = createStackNavigator()
  private[app] lazy val userStackComp: ReactClass = new FunctionComponent[Unit] {
    protected def render(props: Props): ReactElement = {
      <(UserStack.Navigator)(
        ^.initialRouteName := "Profile",
        ^.screenOptions := new StackScreenOptions {
          override val headerShown = false
        }
      )(
        <(UserStack.Screen)(^.name := "Profile", ^.component := userController())()
      )
    }
  }.apply()
}
