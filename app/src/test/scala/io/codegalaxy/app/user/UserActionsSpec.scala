package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.user.UserActionsSpec._
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserLoggedinAction when userLogin" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val profileResp = mock[UserProfileData]
    val userResp = mock[UserData]
    val loginData = UserLoginState(profileResp, userResp)

    //then
    (api.authenticate _).expects(username, password).returning(Future.successful(()))
    (api.getUserProfile _).expects(true).returning(Future.successful(Some(profileResp)))
    (api.getUser _).expects().returning(Future.successful(userResp))
    dispatch.expects(UserLoggedinAction(Some(loginData)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userLogin(dispatch, username, password)

    //then
    message shouldBe "Authenticate User"
    future.map { resp =>
      resp shouldBe Some(loginData)
    }
  }
  
  it should "dispatch UserLoggedinAction(Some) if successful when userLoginFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val profileResp = mock[UserProfileData]
    val userResp = mock[UserData]
    val loginData = UserLoginState(profileResp, userResp)

    //then
    (api.getUserProfile _).expects(true).returning(Future.successful(Some(profileResp)))
    (api.getUser _).expects().returning(Future.successful(userResp))
    dispatch.expects(UserLoggedinAction(Some(loginData)))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userLoginFetch(dispatch)

    //then
    message shouldBe "Fetching UserProfile"
    future.map { resp =>
      resp shouldBe Some(loginData)
    }
  }
  
  it should "dispatch UserLoggedinAction(None) if non-successful when userLoginFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]

    //then
    (api.getUserProfile _).expects(true).returning(Future.successful(None))
    dispatch.expects(UserLoggedinAction(None))

    //when
    val UserLoginAction(FutureTask(message, future)) =
      actions.userLoginFetch(dispatch)

    //then
    message shouldBe "Fetching UserProfile"
    future.map { resp =>
      resp shouldBe None
    }
  }
  
  it should "dispatch UserLoggedoutAction when userLogout" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]

    //then
    (api.logout _).expects().returning(Future.successful(()))
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

  private class UserActionsTest(api: UserApi)
    extends UserActions {

    protected def client: UserApi = api
  }
}
