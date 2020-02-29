package io.codegalaxy.app.auth

import io.codegalaxy.app.CodeGalaxyStateDef
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class AuthControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[AuthActions]
    val controller = new AuthController(actions)
    
    //when & then
    controller.uiComponent shouldBe AuthScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[AuthActions]
    val controller = new AuthController(actions)
    val state = mock[CodeGalaxyStateDef]
    val nav = mock[Navigation]
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case AuthScreenProps(resDispatch, resActions, _) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        
        //TODO: asset onSuccessfulLogin callback
    }
  }
}
