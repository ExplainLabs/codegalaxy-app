package io.codegalaxy.app

import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.app.BaseStateAndRouteController

class CodeGalaxyRootController(onAppReady: () => Unit, actions: CodeGalaxyActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, CodeGalaxyRootProps] {

  lazy val uiComponent: UiComponent[CodeGalaxyRootProps] = new CodeGalaxyRoot(actions)

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): CodeGalaxyRootProps = {
    CodeGalaxyRootProps(
      dispatch = dispatch,
      userActions = actions,
      topicActions = actions,
      state = state,
      onAppReady = onAppReady
    )
  }
}
