package io.codegalaxy.app.user

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.user.UserActions.UserProfileFetchedAction
import scommons.react.test.TestSpec

class UserStateReducerSpec extends TestSpec {

  private val reduce = UserStateReducer.apply _

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe UserState()
  }

  it should "set profile when UserProfileFetchedAction" in {
    //given
    val profile = mock[UserProfileData]

    //when & then
    reduce(Some(UserState()), UserProfileFetchedAction(profile)) shouldBe UserState(
      profile = Some(profile)
    )
  }
}
