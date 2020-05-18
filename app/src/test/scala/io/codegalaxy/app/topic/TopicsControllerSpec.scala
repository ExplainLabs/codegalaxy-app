package io.codegalaxy.app.topic

import io.codegalaxy.app.CodeGalaxyStateDef
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class TopicsControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[TopicActions]
    val controller = new TopicsController(actions)
    
    //when & then
    controller.uiComponent shouldBe TopicsScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[TopicActions]
    val controller = new TopicsController(actions)
    val state = mock[CodeGalaxyStateDef]
    val topicState = mock[TopicState]
    val nav = mock[Navigation]

    (state.topicState _).expects().returning(topicState)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case TopicsScreenProps(resDispatch, resActions, resData) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe topicState
    }
  }
}
