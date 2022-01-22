package io.codegalaxy.app.user

import io.codegalaxy.app.{MockCodeGalaxyState, MockNavigation}
import scommons.react.redux.Dispatch
import scommons.react.test.TestSpec

class UserControllerSpec extends TestSpec {

  //noinspection TypeAnnotation
  class State {
    val userState = mockFunction[UserState]

    val state = new MockCodeGalaxyState(userStateMock = userState)
  }

  it should "return component" in {
    //given
    val actions = new MockUserActions
    val controller = new UserController(actions)
    
    //when & then
    controller.uiComponent shouldBe UserScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = new MockUserActions
    val controller = new UserController(actions)
    val state = new State
    val userState = UserState()
    val nav = new MockNavigation

    state.userState.expects().returning(userState)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state.state, nav)
    
    //then
    inside(result) {
      case UserScreenProps(resDispatch, resActions, resData) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe userState
    }
  }
}
