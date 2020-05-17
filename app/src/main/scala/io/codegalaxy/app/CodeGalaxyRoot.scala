package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.user._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.react.navigation.stack._
import scommons.react.navigation.tab.TabBarOptions.LabelPosition
import scommons.react.navigation.tab._
import scommons.reactnative._

import scala.scalajs.js

case class CodeGalaxyRootProps(dispatch: Dispatch,
                               actions: UserActions,
                               state: UserState,
                               onAppReady: () => Unit)

class CodeGalaxyRoot(actions: CodeGalaxyActions) extends FunctionComponent[CodeGalaxyRootProps] {

  private[app] lazy val Tab = createBottomTabNavigator()
  private[app] lazy val Stack = createStackNavigator()

  private[app] lazy val loginController = new LoginController(actions)
  private lazy val userController = new UserController(actions)
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val showLogin = props.state.login.isEmpty
    
    <(WithAutoLogin())(^.wrapped := WithAutoLoginProps(props.dispatch, props.actions, props.onAppReady))(
      <.NavigationContainer()(
        if (showLogin) {
          <(Stack.Navigator)(
            ^.screenOptions := new StackScreenOptions {
              override val headerShown = false
            }
          )(
            <(Stack.Screen)(^.name := "Login", ^.component := loginController())()
          )
        }
        else {
          <(Tab.Navigator)(
            ^.initialRouteName := "Courses",
            ^.tabBarOptions := new TabBarOptions {
              override val labelPosition = LabelPosition.`below-icon`
            }
          )(
            <(Tab.Screen)(
              ^.name := "Courses",
              ^.component := emptyComp,
              ^.options := new TabScreenOptions {
                override val tabBarIcon = { params =>
                  <(CodeGalaxyIcons.FontAwesome5)(^.name := "list", ^.rnSize := params.size, ^.color := params.color)()
                }: js.Function1[TabBarIconParams, ReactElement]
              }
            )(),
            <(Tab.Screen)(
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
      )
    )
  }

  private[app] lazy val userStackComp: ReactClass = UserScreen.userStackComp(userController)
  
  private[app] lazy val emptyComp: ReactClass = new FunctionComponent[Unit] {
    protected def render(props: Props): ReactElement = {
      <.Text()("//TODO: add content")
    }
  }.apply()
}
