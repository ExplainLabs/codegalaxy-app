package io.codegalaxy.app.question

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.topic.TopicParams
import scommons.react._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.app.BaseStateAndRouteController

class QuestionController(actions: QuestionActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, QuestionScreenProps] {

  lazy val uiComponent: UiComponent[QuestionScreenProps] = QuestionScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): QuestionScreenProps = {
    
    val params = TopicParams.fromMap(nav.getParams)
    
    QuestionScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = state.questionState,
      params = params
    )
  }
}
