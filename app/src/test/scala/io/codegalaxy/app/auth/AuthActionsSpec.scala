package io.codegalaxy.app.auth

import io.codegalaxy.api.auth._
import io.codegalaxy.app.auth.AuthActions._
import io.codegalaxy.app.auth.AuthActionsSpec._
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec

import scala.concurrent.Future

class AuthActionsSpec extends AsyncTestSpec {

  it should "dispatch AuthAuthenticatedAction when authenticate" in {
    //given
    val api = mock[AuthApi]
    val actions = new AuthActionsTest(api)
    val dispatch = mockFunction[Any, Any]
    val username = "test-username"
    val password = "test-password"
    val expectedResp = "some test resp"

    (api.authenticate _).expects(username, password)
      .returning(Future.successful(expectedResp))
    dispatch.expects(AuthAuthenticatedAction(expectedResp))

    //when
    val AuthAuthenticateAction(FutureTask(message, future)) =
      actions.authenticate(dispatch, username, password)

    //then
    message shouldBe "Authenticate"
    future.map { resp =>
      resp shouldBe expectedResp
    }
  }
}

object AuthActionsSpec {

  private class AuthActionsTest(api: AuthApi)
    extends AuthActions {

    protected def client: AuthApi = api
  }
}
