package io.codegalaxy.app.user

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.config.ConfigActions
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.app.BaseStateAndRouteController

class UserController(actions: UserActions with ConfigActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, UserScreenProps] {

  lazy val uiComponent: UiComponent[UserScreenProps] = UserScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): UserScreenProps = {
    UserScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = state.userState
    )
  }
}
