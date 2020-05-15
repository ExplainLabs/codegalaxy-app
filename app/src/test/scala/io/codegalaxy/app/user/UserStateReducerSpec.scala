package io.codegalaxy.app.user

import io.codegalaxy.app.user.UserActions._
import scommons.react.test.TestSpec

class UserStateReducerSpec extends TestSpec {

  private val reduce = UserStateReducer.apply _

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe UserState()
  }

  it should "set profile to Some when UserLoggedinAction" in {
    //given
    val loginData = mock[UserLoginState]

    //when & then
    reduce(Some(UserState()), UserLoggedinAction(Some(loginData))) shouldBe UserState(
      login = Some(loginData)
    )
  }
  
  it should "set profile to None when UserLoggedoutAction" in {
    //given
    val loginData = mock[UserLoginState]

    //when & then
    reduce(Some(UserState(login = Some(loginData))), UserLoggedoutAction()) shouldBe UserState(
      login = None
    )
  }
}
