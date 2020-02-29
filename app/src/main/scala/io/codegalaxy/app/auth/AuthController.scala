package io.codegalaxy.app.auth

import io.codegalaxy.app.CodeGalaxyStateDef
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.reactnative.app.BaseStateAndRouteController

class AuthController(actions: AuthActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, AuthScreenProps] {

  lazy val uiComponent: UiComponent[AuthScreenProps] = AuthScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): AuthScreenProps = {
    AuthScreenProps(
      dispatch = dispatch,
      actions = actions,
      onSuccessfulLogin = { () =>
        //TODO: navigate from AuthScreen
      }
    )
  }
}
