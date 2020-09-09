package io.codegalaxy.app.chapter

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.topic.TopicParams
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class ChapterListControllerSpec extends TestSpec {

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
    val state = mock[CodeGalaxyStateDef]
    val chapterState = mock[ChapterState]
    val nav = mock[Navigation]
    val chapter = "test_chapter"
    val params = TopicParams("test_topic")

    (state.chapterState _).expects().returning(chapterState)
    (nav.getParams _).expects().returning(params.toMap)
    (nav.navigate(_: String, _: Map[String, String]))
      .expects("Question", params.copy(chapter = Some(chapter)).toMap)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
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
