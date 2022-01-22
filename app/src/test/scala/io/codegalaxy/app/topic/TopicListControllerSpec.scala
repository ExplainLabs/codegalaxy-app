package io.codegalaxy.app.topic

import io.codegalaxy.app.{MockCodeGalaxyState, MockNavigation}
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class TopicListControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val topicState = mockFunction[TopicState]

    val state = new MockCodeGalaxyState(topicStateMock = topicState)
  }

  //noinspection TypeAnnotation
  class Navigation {
    val navigate2 = mockFunction[String, Map[String, String], Unit]

    val nav = new MockNavigation(navigate2Mock = navigate2)
  }

  it should "return component" in {
    //given
    val actions = new MockTopicActions
    val controller = new TopicListController(actions)
    
    //when & then
    controller.uiComponent shouldBe TopicListScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new MockTopicActions
    val controller = new TopicListController(actions)
    val state = new State
    val topicState = TopicState()
    val nav = new Navigation
    val params = TopicParams("test_topic")

    state.topicState.expects().returning(topicState)
    nav.navigate2.expects(*, *).onCall { (n, p) =>
      n shouldBe "Quiz"
      p shouldBe params.toMap
    }
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, nav.nav)
    
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
