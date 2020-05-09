package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.reactnative._
import scommons.react.navigation._
import scommons.react.navigation.tab.TabBarOptions.LabelPosition
import scommons.react.navigation.tab._

import scala.scalajs.js

case class CodeGalaxyRootProps(dispatch: Dispatch,
                               actions: UserActions,
                               state: UserState,
                               onAppReady: () => Unit)

object CodeGalaxyRoot extends FunctionComponent[CodeGalaxyRootProps] {

  private[app] lazy val Tab = createBottomTabNavigator()
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val showLogin = props.state.profile.isEmpty
    
    <(WithAutoLogin())(^.wrapped := WithAutoLoginProps(props.dispatch, props.actions, props.onAppReady))(
      if (showLogin) {
        <(LoginScreen())(^.wrapped := LoginScreenProps(onLogin = { (email, password) =>
          props.dispatch(props.actions.userAuth(props.dispatch, email, password))
        }))()
      }
      else {
        <.NavigationContainer()(
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
              ^.component := emptyComp,
              ^.options := new TabScreenOptions {
                override val tabBarIcon = { params =>
                  <(CodeGalaxyIcons.FontAwesome5)(^.name := "user", ^.rnSize := params.size, ^.color := params.color)()
                }: js.Function1[TabBarIconParams, ReactElement]
              }
            )()
          )
        )
      }
    )
  }
  
  private[app] lazy val emptyComp: ReactClass = new FunctionComponent[Unit] {
    
    protected def render(props: Props): ReactElement = {
      <.Text()("//TODO: add content")
    }
  }.apply()
}
