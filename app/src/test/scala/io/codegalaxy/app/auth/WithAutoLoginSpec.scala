package io.codegalaxy.app.auth

import io.codegalaxy.app.user.UserActions.UserLoginAction
import io.codegalaxy.app.user.{UserActions, UserLoginState}
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test._

import scala.concurrent.Future

class WithAutoLoginSpec extends AsyncTestSpec
  with BaseTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch action then render children and call onReady if ready" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val onReady = mockFunction[Unit]
    val loginData = Some(mock[UserLoginState])
    val props = WithAutoLoginProps(dispatch, actions, onReady)

    val fetchAction = UserLoginAction(
      FutureTask("Fetching User", Future.successful(loginData))
    )
    (actions.userLoginFetch _).expects(dispatch).returning(fetchAction)

    //then
    dispatch.expects(fetchAction)

    //when
    val renderer = createTestRenderer(<(WithAutoLogin())(^.wrapped := props)(
      <.>()("initial child")
    ))
    
    //then
    renderer.root.children.toList shouldBe Nil
    onReady.expects()

    //when
    fetchAction.task.future.map { _ =>
      //then
      TestRenderer.act { () =>
        renderer.root.children.toList shouldBe List("initial child")
      }
      
      //when
      TestRenderer.act { () =>
        renderer.update(<(WithAutoLogin())(^.wrapped := props)(
          <.>()("updated child")
        ))
      }
      //then
      TestRenderer.act { () =>
        renderer.root.children.toList shouldBe List("updated child")
      }

      //cleanup
      renderer.unmount()
      Succeeded
    }
  }
  
  it should "render initially empty component" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val props = WithAutoLoginProps(dispatch, actions, onReady = () => ())

    //when
    val result = shallowRender(<(WithAutoLogin())(^.wrapped := props)(
      <.>()("test child")
    ))

    //then
    assertNativeComponent(result,
      <.>()()
    )
  }
}
