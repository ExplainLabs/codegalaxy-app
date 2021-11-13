package io.codegalaxy.app.auth

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.user.UserActions
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.app.BaseStateAndRouteController

class LoginController(actions: UserActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, LoginScreenProps] {

  lazy val uiComponent: UiComponent[LoginScreenProps] = LoginScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): LoginScreenProps = {
    
    LoginScreenProps(
      onLogin = { (email, password) =>
        dispatch(actions.userLogin(dispatch, email, password))
      },
      onSignup = { () =>
        dispatch(actions.userSignup())
      }
    )
  }
}
