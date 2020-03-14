package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.app.user.UserActionsSpec._
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec

import scala.concurrent.Future

class UserActionsSpec extends AsyncTestSpec {

  it should "dispatch UserProfileFetchedAction when userAuth" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val expectedResp = Some(mock[UserProfileData])

    //then
    (api.authenticate _).expects(username, password).returning(Future.successful(()))
    (api.getUserProfile _).expects(true).returning(Future.successful(expectedResp))
    dispatch.expects(UserProfileFetchedAction(expectedResp))

    //when
    val UserProfileFetchAction(FutureTask(message, future)) =
      actions.userAuth(dispatch, username, password)

    //then
    message shouldBe "Authenticate User"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
  
  it should "dispatch UserProfileFetchedAction when userProfileFetch" in {
    //given
    val api = mock[UserApi]
    val actions = new UserActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val expectedResp = Some(mock[UserProfileData])

    //then
    (api.getUserProfile _).expects(true).returning(Future.successful(expectedResp))
    dispatch.expects(UserProfileFetchedAction(expectedResp))

    //when
    val UserProfileFetchAction(FutureTask(message, future)) =
      actions.userProfileFetch(dispatch)

    //then
    message shouldBe "Fetching UserProfile"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object UserActionsSpec {

  private class UserActionsTest(api: UserApi)
    extends UserActions {

    protected def client: UserApi = api
  }
}
