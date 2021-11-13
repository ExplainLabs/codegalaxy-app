package io.codegalaxy.app.question

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.topic.TopicParams
import scommons.react.navigation.Navigation
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class QuestionControllerSpec extends TestSpec {

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
    val state = mock[CodeGalaxyStateDef]
    val questionState = mock[QuestionState]
    val nav = mock[Navigation]
    val params = TopicParams("test_topic", Some("test_chpater"))

    (state.questionState _).expects().returning(questionState)
    (nav.getParams _).expects().returning(params.toMap)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
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
