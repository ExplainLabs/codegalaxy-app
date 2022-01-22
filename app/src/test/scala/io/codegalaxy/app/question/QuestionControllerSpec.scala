package io.codegalaxy.app.question

import io.codegalaxy.app.topic.TopicParams
import io.codegalaxy.app.{MockCodeGalaxyState, MockNavigation}
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class QuestionControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val questionState = mockFunction[QuestionState]
    
    val state = new MockCodeGalaxyState(questionStateMock = questionState)
  }
  
  //noinspection TypeAnnotation
  class Nav {
    val getParams = mockFunction[Map[String, String]]
    
    val nav = new MockNavigation(getParamsMock = getParams)
  }
  
  it should "return component" in {
    //given
    val actions = mock[QuestionActions]
    val controller = new QuestionController(actions)
    
    //when & then
    controller.uiComponent shouldBe QuestionScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[QuestionActions]
    val controller = new QuestionController(actions)
    val state = new State
    val questionState = QuestionState()
    val nav = new Nav
    val params = TopicParams("test_topic", Some("test_chpater"))

    state.questionState.expects().returning(questionState)
    nav.getParams.expects().returning(params.toMap)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, nav.nav)
    
    //then
    inside(result) {
      case QuestionScreenProps(resDispatch, resActions, resData, resParams) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe questionState
        resParams shouldBe params
    }
  }
}
