package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyStateDef
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.reactnative.app.BaseStateAndRouteController

class TopicsController(actions: TopicActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, TopicsScreenProps] {

  lazy val uiComponent: UiComponent[TopicsScreenProps] = TopicsScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): TopicsScreenProps = {
    TopicsScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = state.topicState
    )
  }
}
