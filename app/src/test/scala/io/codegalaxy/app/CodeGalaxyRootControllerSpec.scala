package io.codegalaxy.app

import io.codegalaxy.app.user.{UserActions, UserState}
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class CodeGalaxyRootControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[UserActions]
    val controller = new CodeGalaxyRootController(onAppReady = () => (), actions)
    
    //when & then
    controller.uiComponent shouldBe CodeGalaxyRoot
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserActions]
    val onAppReady = mockFunction[Unit]
    val controller = new CodeGalaxyRootController(onAppReady, actions)
    val state = mock[CodeGalaxyStateDef]
    val userState = mock[UserState]
    val nav = mock[Navigation]

    (state.userState _).expects().returning(userState)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case CodeGalaxyRootProps(resDispatch, resActions, resState, resOnAppReady, _) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resState shouldBe userState
        resOnAppReady shouldBe onAppReady
        
        //TODO: asset onSuccessfulLogin callback
    }
  }
}
