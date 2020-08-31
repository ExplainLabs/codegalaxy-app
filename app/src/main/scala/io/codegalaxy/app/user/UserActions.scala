package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.ProfileEntity
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait UserActions {

  protected def client: UserApi
  protected def userService: UserService

  def userLogin(dispatch: Dispatch, user: String, password: String): UserLoginAction = {
    val future = for {
      _ <- client.authenticate(user, password)
      profile <- userService.fetchProfile(refresh = true)
    } yield profile
    
    val resultF = future.andThen {
      case Success(profile) => dispatch(UserLoggedinAction(profile))
    }

    UserLoginAction(FutureTask("Authenticate User", resultF))
  }
  
  def userProfileFetch(dispatch: Dispatch): UserLoginAction = {
    val resultF = userService.fetchProfile().andThen {
      case Success(profile) => dispatch(UserLoggedinAction(profile))
    }

    UserLoginAction(FutureTask("Fetching Profile", resultF))
  }
  
  def userLogout(dispatch: Dispatch): UserLogoutAction = {
    val resultF = for {
      _ <- client.logout()
      _ <- userService.removeProfile()
    } yield ()
    
    resultF.andThen {
      case Success(_) => dispatch(UserLoggedoutAction())
    }

    UserLogoutAction(FutureTask("Logout User", resultF))
  }
}

object UserActions {

  case class UserLoginAction(task: FutureTask[Option[ProfileEntity]]) extends TaskAction
  case class UserLoggedinAction(data: Option[ProfileEntity]) extends Action
  
  case class UserLogoutAction(task: FutureTask[Unit]) extends TaskAction
  case class UserLoggedoutAction() extends Action
}
