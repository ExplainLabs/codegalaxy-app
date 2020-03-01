package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait UserActions {

  protected def client: UserApi

  def userAuth(dispatch: Dispatch, user: String, password: String): UserProfileFetchAction = {
    val future = for {
      _ <- client.authenticate(user, password)
      profile <- client.getUserProfile(force = true)
    } yield profile
    
    val resultF = future.andThen {
      case Success(profile) => dispatch(UserProfileFetchedAction(profile))
    }

    UserProfileFetchAction(FutureTask("Authenticate User", resultF))
  }
  
  def userProfileFetch(dispatch: Dispatch): UserProfileFetchAction = {
    val future = client.getUserProfile(force = true).andThen {
      case Success(data) => dispatch(UserProfileFetchedAction(data))
    }

    UserProfileFetchAction(FutureTask("Fetching UserProfile", future))
  }
}

object UserActions {

  case class UserProfileFetchAction(task: FutureTask[UserProfileData]) extends TaskAction
  case class UserProfileFetchedAction(data: UserProfileData) extends Action
}
