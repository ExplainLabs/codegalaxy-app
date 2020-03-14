package io.codegalaxy.app

import io.codegalaxy.api.user.UserProfileData
import io.codegalaxy.app.user.UserActions.UserProfileFetchAction
import io.codegalaxy.app.user.{UserActions, UserState}
import org.scalatest.Succeeded
import scommons.react._
import scommons.react.redux.task.FutureTask
import scommons.react.test.dom.AsyncTestSpec
import scommons.react.test.raw.TestRenderer
import scommons.react.test.util.{ShallowRendererUtils, TestRendererUtils}
import scommons.reactnative.raw.TouchableOpacity

import scala.concurrent.Future

class CodeGalaxyRootSpec extends AsyncTestSpec
  with ShallowRendererUtils
  with TestRendererUtils {

  it should "dispatch actions and show LoginScreen if not logged-in when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val onAppReady = mockFunction[Unit]
    //not logged-in
    val userProfile = Option.empty[UserProfileData]
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(userProfile), onAppReady)

    val fetchAction = UserProfileFetchAction(
      FutureTask("Fetching User", Future.successful(userProfile))
    )
    (actions.userProfileFetch _).expects(dispatch).returning(fetchAction)

    //then
    dispatch.expects(fetchAction)

    //when
    val renderer = createTestRenderer(<(CodeGalaxyRoot())(^.wrapped := props)())
    
    //then
    onAppReady.expects()
    
    findComponents(renderer.root, TouchableOpacity) shouldBe Nil

    fetchAction.task.future.map { _ =>
      //then
      TestRenderer.act { () =>
        val List(_) = findComponents(renderer.root, TouchableOpacity)
      }
      
      //cleanup
      renderer.unmount()
      Succeeded
    }
  }
  
  it should "dispatch actions and don't show LoginScreen if logged-in when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val onAppReady = mockFunction[Unit]
    //logged-in
    val userProfile = Some(mock[UserProfileData])
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(userProfile), onAppReady)

    val fetchAction = UserProfileFetchAction(
      FutureTask("Fetching User", Future.successful(userProfile))
    )
    (actions.userProfileFetch _).expects(dispatch).returning(fetchAction)

    //then
    dispatch.expects(fetchAction)

    //when
    val renderer = createTestRenderer(<(CodeGalaxyRoot())(^.wrapped := props)())
    
    //then
    onAppReady.expects()

    fetchAction.task.future.map { _ =>
      //then
      TestRenderer.act { () =>
        findComponents(renderer.root, TouchableOpacity) shouldBe Nil
      }
      
      //cleanup
      renderer.unmount()
      Succeeded
    }
  }
  
  ignore should "dispatch actions when onPress login" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[UserActions]
    val onAppReady = mockFunction[Unit]
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(), onAppReady)
    //not logged-in
    val userData = Option.empty[UserProfileData]

    val fetchAction = UserProfileFetchAction(
      FutureTask("Fetching User", Future.successful(userData))
    )
    (actions.userProfileFetch _).expects(dispatch).returning(fetchAction)

    //then
    dispatch.expects(fetchAction)

    //when
    val renderer = createTestRenderer(<(CodeGalaxyRoot())(^.wrapped := props)())
    
    //then
    onAppReady.expects()
    
    findComponents(renderer.root, TouchableOpacity) shouldBe Nil

    fetchAction.task.future.map { _ =>
      //then
      TestRenderer.act { () =>
        val List(loginBtn) = findComponents(renderer.root, TouchableOpacity)
        //when
        loginBtn.props.onPress()
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
    val props = CodeGalaxyRootProps(dispatch, actions, UserState(), onAppReady = () => ())

    //when
    val result = shallowRender(<(CodeGalaxyRoot())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.>()()
    )
  }
}
