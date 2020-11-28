package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.config.ConfigService
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.user.UserActionsSpec._
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserLoggedinAction when userLogin" in {
    //given
    val api = mock[UserApi]
    val userService = mock[UserService]
    val configService = mock[ConfigService]
    val actions = new UserActionsTest(api, userService, configService)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profile = ProfileEntity(
      id = 123,
      username = "test_username",
      email = Some("Test email"),
      firstName = Some("Test firstName"),
      lastName = Some("Test lastName"),
      fullName = Some("Test fullName"),
      city = Some("Test City"),
      avatarUrl = Some("Test avatarUrl")
    )
    val config = mock[ConfigEntity]

    //then
    (api.authenticate _).expects(username, password).returning(Future.successful(()))
    (userService.fetchProfile _).expects(true).returning(Future.successful(Some(profile)))
    (configService.getConfig _).expects(profile.id).returning(Future.successful(Some(config)))
    dispatch.expects(UserLoggedinAction(Some(profile), Some(config)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userLogin(dispatch, username, password)

    //then
    message shouldBe "Authenticate User"
    future.map { resp =>
      resp shouldBe Some(profile) -> Some(config)
    }
  }
  
  it should "dispatch UserLoggedinAction(Some) if successful when userProfileFetch" in {
    //given
    val api = mock[UserApi]
    val userService = mock[UserService]
    val configService = mock[ConfigService]
    val actions = new UserActionsTest(api, userService, configService)
    val dispatch = mockFunction[Any, Any]
    val profile = ProfileEntity(
      id = 123,
      username = "test_username",
      email = Some("Test email"),
      firstName = Some("Test firstName"),
      lastName = Some("Test lastName"),
      fullName = Some("Test fullName"),
      city = Some("Test City"),
      avatarUrl = Some("Test avatarUrl")
    )
    val config = mock[ConfigEntity]

    //then
    (userService.fetchProfile _).expects(false).returning(Future.successful(Some(profile)))
    (configService.getConfig _).expects(profile.id).returning(Future.successful(Some(config)))
    dispatch.expects(UserLoggedinAction(Some(profile), Some(config)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userProfileFetch(dispatch)

    //then
    message shouldBe "Fetching Profile"
    future.map { resp =>
      resp shouldBe Some(profile) -> Some(config)
    }
  }
  
  it should "dispatch UserLoggedinAction(None) if non-successful when userProfileFetch" in {
    //given
    val api = mock[UserApi]
    val userService = mock[UserService]
    val configService = mock[ConfigService]
    val actions = new UserActionsTest(api, userService, configService)
    val dispatch = mockFunction[Any, Any]

    //then
    (userService.fetchProfile _).expects(false).returning(Future.successful(None))
    dispatch.expects(UserLoggedinAction(None, None))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userProfileFetch(dispatch)

    //then
    message shouldBe "Fetching Profile"
    future.map { resp =>
      resp shouldBe None -> None
    }
  }
  
  it should "dispatch UserLoggedoutAction when userLogout" in {
    //given
    val api = mock[UserApi]
    val userService = mock[UserService]
    val configService = mock[ConfigService]
    val actions = new UserActionsTest(api, userService, configService)
    val dispatch = mockFunction[Any, Any]

    //then
    (api.logout _).expects().returning(Future.successful(()))
    (userService.removeProfile _).expects().returning(Future.successful(()))
    dispatch.expects(UserLoggedoutAction())

    //when
    val UserLogoutAction(FutureTask(message, future)) =
      actions.userLogout(dispatch)

    //then
    message shouldBe "Logout User"
    future.map { _ =>
      Succeeded
    }
  }
}

object UserActionsSpec {

  private class UserActionsTest(api: UserApi,
                                userServiceMock: UserService,
                                configServiceMock: ConfigService
                               ) extends UserActions {

    protected def client: UserApi = api
    protected def userService: UserService = userServiceMock
    protected def configService: ConfigService = configServiceMock
  }
}
