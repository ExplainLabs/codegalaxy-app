package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.user.UserActionsSpec._
import io.codegalaxy.domain.ProfileEntity
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserLoggedinAction when userLogin" in {
    //given
    val api = mock[UserApi]
    val service = mock[UserService]
    val actions = new UserActionsTest(api, service)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profile = mock[ProfileEntity]

    //then
    (api.authenticate _).expects(username, password).returning(Future.successful(()))
    (service.fetchProfile _).expects(true).returning(Future.successful(Some(profile)))
    dispatch.expects(UserLoggedinAction(Some(profile)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userLogin(dispatch, username, password)

    //then
    message shouldBe "Authenticate User"
    future.map { resp =>
      resp shouldBe Some(profile)
    }
  }
  
  it should "dispatch UserLoggedinAction(Some) if successful when userProfileFetch" in {
    //given
    val api = mock[UserApi]
    val service = mock[UserService]
    val actions = new UserActionsTest(api, service)
    val dispatch = mockFunction[Any, Any]
    val profile = mock[ProfileEntity]

    //then
    (service.fetchProfile _).expects(false).returning(Future.successful(Some(profile)))
    dispatch.expects(UserLoggedinAction(Some(profile)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userProfileFetch(dispatch)

    //then
    message shouldBe "Fetching Profile"
    future.map { resp =>
      resp shouldBe Some(profile)
    }
  }
  
  it should "dispatch UserLoggedinAction(None) if non-successful when userProfileFetch" in {
    //given
    val api = mock[UserApi]
    val service = mock[UserService]
    val actions = new UserActionsTest(api, service)
    val dispatch = mockFunction[Any, Any]

    //then
    (service.fetchProfile _).expects(false).returning(Future.successful(None))
    dispatch.expects(UserLoggedinAction(None))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userProfileFetch(dispatch)

    //then
    message shouldBe "Fetching Profile"
    future.map { resp =>
      resp shouldBe None
    }
  }
  
  it should "dispatch UserLoggedoutAction when userLogout" in {
    //given
    val api = mock[UserApi]
    val service = mock[UserService]
    val actions = new UserActionsTest(api, service)
    val dispatch = mockFunction[Any, Any]

    //then
    (api.logout _).expects().returning(Future.successful(()))
    (service.removeProfile _).expects().returning(Future.successful(()))
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

  private class UserActionsTest(api: UserApi, service: UserService)
    extends UserActions {

    protected def client: UserApi = api
    protected def userService: UserService = service
  }
}
