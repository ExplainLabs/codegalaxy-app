package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.MockImage
import io.codegalaxy.app.config.{ConfigService, MockConfigService}
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.user.UserActionsSpec._
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask
import scommons.reactnative.Image

import scala.concurrent.Future
import scala.scalajs.js

class UserActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class UserApi {
    val authenticate = mockFunction[String, String, Future[Unit]]
    val logout = mockFunction[Future[Unit]]
    
    val api = new MockUserApi(
      authenticateMock = authenticate,
      logoutMock = logout
    )
  }
  
  //noinspection TypeAnnotation
  class UserService {
    val fetchProfile = mockFunction[Boolean, Future[Option[ProfileEntity]]]
    val removeProfile = mockFunction[Future[Unit]]
    
    val service = new MockUserService(
      fetchProfileMock = fetchProfile,
      removeProfileMock = removeProfile
    )
  }
  
  //noinspection TypeAnnotation
  class ConfigService {
    val getConfig = mockFunction[Int, Future[Option[ConfigEntity]]]
    
    val service = new MockConfigService(getConfigMock = getConfig)
  }
  
  //noinspection TypeAnnotation
  class Image {
    val prefetch = mockFunction[String, Future[js.Any]]
    
    val img = new MockImage(prefetchMock = prefetch)
  }
  
  it should "dispatch UserLoggedinAction when userLogin" in {
    //given
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profile = getProfileEntity
    val config = ConfigEntity(userId = 123, darkTheme = false)

    //then
    api.authenticate.expects(*, *).onCall { (u, p) =>
      u shouldBe username
      p shouldBe password
      Future.successful(())
    }
    userService.fetchProfile.expects(*).onCall { (refresh: Boolean) =>
      refresh shouldBe true
      Future.successful(Some(profile))
    }
    configService.getConfig.expects(*).onCall { (id: Int) =>
      id shouldBe profile.id
      Future.successful(Some(config))
    }
    image.prefetch.expects(*).onCall { (url: String) =>
      url shouldBe profile.avatarUrl.get
      Future.successful(().asInstanceOf[js.Any])
    }
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
  
  it should "return UserSignupAction when userSignup" in {
    //given
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)

    //when
    val UserSignupAction(FutureTask(message, future)) =
      actions.userSignup()

    //then
    message shouldBe "Signing up User"
    future.map { _ =>
      Succeeded
    }
  }
  
  it should "dispatch UserLoggedinAction if no profile image url when userLogin" in {
    //given
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profile = getProfileEntity.copy(avatarUrl = None)
    val config = ConfigEntity(userId = 123, darkTheme = false)

    //then
    api.authenticate.expects(*, *).onCall { (u, p) =>
      u shouldBe username
      p shouldBe password
      Future.successful(())
    }
    userService.fetchProfile.expects(*).onCall { (refresh: Boolean) =>
      refresh shouldBe true
      Future.successful(Some(profile))
    }
    configService.getConfig.expects(*).onCall { id: Int =>
      id shouldBe profile.id
      Future.successful(Some(config))
    }
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
  
  it should "dispatch UserLoggedinAction if profile image error when userLogin" in {
    //given
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profile = getProfileEntity
    val config = ConfigEntity(userId = 123, darkTheme = false)

    //then
    api.authenticate.expects(*, *).onCall { (u, p) =>
      u shouldBe username
      p shouldBe password
      Future.successful(())
    }
    userService.fetchProfile.expects(*).onCall { refresh: Boolean =>
      refresh shouldBe true
      Future.successful(Some(profile))
    }
    configService.getConfig.expects(*).onCall { id: Int =>
      id shouldBe profile.id
      Future.successful(Some(config))
    }
    image.prefetch.expects(*).onCall { url: String =>
      url shouldBe profile.avatarUrl.get
      Future.failed(new Exception("test image error"))
    }
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
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]
    val profile = getProfileEntity
    val config = ConfigEntity(userId = 123, darkTheme = false)

    //then
    userService.fetchProfile.expects(*).onCall { refresh: Boolean =>
      refresh shouldBe false
      Future.successful(Some(profile))
    }
    configService.getConfig.expects(*).onCall { id: Int =>
      id shouldBe profile.id
      Future.successful(Some(config))
    }
    image.prefetch.expects(*).onCall { url: String =>
      url shouldBe profile.avatarUrl.get
      Future.successful(().asInstanceOf[js.Any])
    }
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
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]

    //then
    userService.fetchProfile.expects(*).onCall { refresh: Boolean =>
      refresh shouldBe false
      Future.successful(None)
    }
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
    val api = new UserApi
    val userService = new UserService
    val configService = new ConfigService
    val image = new Image
    val actions = new UserActionsTest(api.api, userService.service, configService.service, image.img)
    val dispatch = mockFunction[Any, Any]

    //then
    api.logout.expects().returning(Future.successful(()))
    userService.removeProfile.expects().returning(Future.successful(()))
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

  private def getProfileEntity: ProfileEntity = {
    ProfileEntity(
      id = 123,
      username = "test_username",
      email = Some("Test email"),
      firstName = Some("Test firstName"),
      lastName = Some("Test lastName"),
      fullName = Some("Test fullName"),
      city = Some("Test City"),
      avatarUrl = Some("Test avatarUrl")
    )
  }
}

object UserActionsSpec {

  private class UserActionsTest(api: UserApi,
                                userServiceMock: UserService,
                                configServiceMock: ConfigService,
                                imageMock: Image
                               ) extends UserActions {

    protected def client: UserApi = api
    protected def userService: UserService = userServiceMock
    protected def configService: ConfigService = configServiceMock
    protected def image: Image = imageMock
  }
}
