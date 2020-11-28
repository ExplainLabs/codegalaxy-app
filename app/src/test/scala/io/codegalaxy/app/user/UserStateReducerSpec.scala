package io.codegalaxy.app.user

import io.codegalaxy.app.BaseStateReducerSpec
import io.codegalaxy.app.config.ConfigActions.ConfigUpdatedAction
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}

class UserStateReducerSpec extends BaseStateReducerSpec(
  createState = UserState(),
  reduce = UserStateReducer.apply
) {

  it should "set user data to Some when UserLoggedinAction" in {
    //given
    val profile = mock[ProfileEntity]
    val config = mock[ConfigEntity]

    //when & then
    reduce(Some(UserState()), UserLoggedinAction(Some(profile), Some(config))) shouldBe UserState(
      profile = Some(profile),
      config = Some(config)
    )
  }
  
  it should "set user data to None when UserLoggedoutAction" in {
    //given
    val profile = mock[ProfileEntity]
    val config = mock[ConfigEntity]

    //when & then
    reduce(Some(UserState(profile = Some(profile), config = Some(config))), UserLoggedoutAction()) shouldBe {
      UserState(
        profile = None,
        config = None
      )
    }
  }
  
  it should "set darkTheme when ConfigUpdatedAction" in {
    //given
    val config = ConfigEntity(123, darkTheme = true)
    
    //when & then
    reduce(Some(UserState()), ConfigUpdatedAction(config)) shouldBe UserState(
      config = Some(config)
    )
  }
}
