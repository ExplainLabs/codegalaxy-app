package io.codegalaxy.app.chapter

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react._
import scommons.react.navigation._
import scommons.reactnative.app.BaseStateAndRouteController

class ChapterListController(actions: ChapterActions)
  extends BaseStateAndRouteController[CodeGalaxyStateDef, ChapterListScreenProps] {

  lazy val uiComponent: UiComponent[ChapterListScreenProps] = ChapterListScreen

  def mapStateAndRouteToProps(dispatch: Dispatch,
                              state: CodeGalaxyStateDef,
                              nav: Navigation): ChapterListScreenProps = {
    
    val params = TopicParams.fromMap(nav.getParams)
    
    ChapterListScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = state.chapterState,
      params = params,
      navigate = { chapter =>
        nav.navigate("Question", params.copy(chapter = Some(chapter)).toMap)
      }
    )
  }
}
