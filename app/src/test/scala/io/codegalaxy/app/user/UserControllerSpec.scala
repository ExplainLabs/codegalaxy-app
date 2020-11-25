package io.codegalaxy.app.user

import io.codegalaxy.app.CodeGalaxyStateDef
import io.codegalaxy.app.config.ConfigActions
import io.codegalaxy.app.user.UserControllerSpec._
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.navigation.Navigation
import scommons.react.test.TestSpec

class UserControllerSpec extends TestSpec {

  it should "return component" in {
    //given
    val actions = mock[UserAndConfigActions]
    val controller = new UserController(actions)
    
    //when & then
    controller.uiComponent shouldBe UserScreen
  }
  
  it should "map state to props" in {
    //given
    val dispatch = mock[Dispatch]
    val actions = mock[UserAndConfigActions]
    val controller = new UserController(actions)
    val state = mock[CodeGalaxyStateDef]
    val userState = mock[UserState]
    val nav = mock[Navigation]

    (state.userState _).expects().returning(userState)
    
    //when
    val result = controller.mapStateAndRouteToProps(dispatch, state, nav)
    
    //then
    inside(result) {
      case UserScreenProps(resDispatch, resActions, resData) =>
        resDispatch shouldBe dispatch
        resActions shouldBe actions
        resData shouldBe userState
    }
  }
}

object UserControllerSpec {

  private trait UserAndConfigActions
    extends UserActions
    with ConfigActions
}
