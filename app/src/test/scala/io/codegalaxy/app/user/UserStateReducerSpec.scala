package io.codegalaxy.app.user

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.ProfileEntity

class UserStateReducerSpec extends BaseStateReducerSpec(
  createState = UserState(),
  reduce = UserStateReducer.apply
) {

  it should "set profile to Some when UserLoggedinAction" in {
    //given
    val profile = mock[ProfileEntity]

    //when & then
    reduce(Some(UserState()), UserLoggedinAction(Some(profile))) shouldBe UserState(
      profile = Some(profile)
    )
  }
  
  it should "set profile to None when UserLoggedoutAction" in {
    //given
    val profile = mock[ProfileEntity]

    //when & then
    reduce(Some(UserState(profile = Some(profile))), UserLoggedoutAction()) shouldBe UserState(
      profile = None
    )
  }
}
