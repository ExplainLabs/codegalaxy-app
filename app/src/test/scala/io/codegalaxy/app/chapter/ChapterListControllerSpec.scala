package io.codegalaxy.app.chapter

import io.codegalaxy.app.topic.TopicParams
import io.codegalaxy.app.{MockCodeGalaxyState, MockNavigation}
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class ChapterListControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val chapterState = mockFunction[ChapterState]

    val state = new MockCodeGalaxyState(chapterStateMock = chapterState)
  }

  //noinspection TypeAnnotation
  class Navigation {
    val getParams = mockFunction[Map[String, String]]
    val navigate2 = mockFunction[String, Map[String, String], Unit]
    
    val nav = new MockNavigation(
      getParamsMock = getParams,
      navigate2Mock = navigate2
    )
  }
  
  it should "return component" in {
    //given
    val actions = mock[ChapterActions]
    val controller = new ChapterListController(actions)
    
    //when & then
    controller.uiComponent shouldBe ChapterListScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[ChapterActions]
    val controller = new ChapterListController(actions)
    val state = new State
    val chapterState = mock[ChapterState]
    val nav = mock[Navigation]
    val chapter = "test_chapter"
    val params = TopicParams("test_topic")

    state.chapterState.expects().returning(chapterState)
    nav.getParams.expects().returning(params.toMap)
    nav.navigate2.expects(*, *).onCall { (n, p) =>
      n shouldBe "Question"
      p shouldBe params.copy(chapter = Some(chapter)).toMap
    }
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, nav.nav)
    
    //then
    inside(result) {
      case ChapterListScreenProps(resDispatch, resActions, resData, resParams, navigate) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe chapterState
        resParams shouldBe params
        navigate(chapter)
    }
  }
}
