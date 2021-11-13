package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyStateDef
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.app.BaseStateAndRouteController

class TopicListController(actions: TopicActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, TopicListScreenProps] {

  lazy val uiComponent: UiComponent[TopicListScreenProps] = TopicListScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): TopicListScreenProps = {
    TopicListScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = state.topicState,
      navigate = { topic =>
        nav.navigate("Quiz", TopicParams(topic).toMap)
      }
    )
  }
}
