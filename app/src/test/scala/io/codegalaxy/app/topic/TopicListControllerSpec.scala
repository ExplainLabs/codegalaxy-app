package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyStateDef
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class TopicListControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[TopicActions]
    val controller = new TopicListController(actions)
    
    //when & then
    controller.uiComponent shouldBe TopicListScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[TopicActions]
    val controller = new TopicListController(actions)
    val state = mock[CodeGalaxyStateDef]
    val topicState = mock[TopicState]
    val nav = mock[Navigation]
    val params = TopicParams("test_topic")

    (state.topicState _).expects().returning(topicState)
    (nav.navigate(_: String, _: Map[String, String])).expects("Quiz", params.toMap)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case TopicListScreenProps(resDispatch, resActions, resData, navigate) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe topicState
        navigate(params.topic)
    }
  }
}
