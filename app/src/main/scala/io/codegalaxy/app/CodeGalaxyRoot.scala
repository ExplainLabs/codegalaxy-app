package io.codegalaxy.app

import io.codegalaxy.app.auth._
import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.reactnative._

case class CodeGalaxyRootProps(dispatch: Dispatch,
                               actions: UserActions,
                               state: UserState,
                               onAppReady: () => Unit)

object CodeGalaxyRoot extends FunctionComponent[CodeGalaxyRootProps] {

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
        <.Text()("TODO: main screen")
      }
    )
  }
}
