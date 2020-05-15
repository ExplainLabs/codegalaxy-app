package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.user.UserActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

trait UserActions {

  protected def client: UserApi

  def userLogin(dispatch: Dispatch, user: String, password: String): UserLoginAction = {
    val future = for {
      _ <- client.authenticate(user, password)
      loginData <- fetchLoginData()
    } yield loginData
    
    val resultF = future.andThen {
      case Success(profile) => dispatch(UserLoggedinAction(profile))
    }

    UserLoginAction(FutureTask("Authenticate User", resultF))
  }
  
  def userLoginFetch(dispatch: Dispatch): UserLoginAction = {
    val resultF = fetchLoginData().andThen {
      case Success(data) => dispatch(UserLoggedinAction(data))
    }

    UserLoginAction(FutureTask("Fetching UserProfile", resultF))
  }
  
  def userLogout(dispatch: Dispatch): UserLogoutAction = {
    val resultF = client.logout().andThen {
      case Success(_) => dispatch(UserLoggedoutAction())
    }

    UserLogoutAction(FutureTask("Logout User", resultF))
  }

  private def fetchLoginData(): Future[Option[UserLoginState]] = {
    for {
      maybeProfile <- client.getUserProfile(force = true)
      maybeUser <-
        if (maybeProfile.isDefined) client.getUser.map(Some(_))
        else Future.successful(None)
    } yield {
      maybeProfile.flatMap { profile =>
        maybeUser.map { user =>
          UserLoginState(
            profile = profile,
            user = user
          )
        }
      }
    }
  }
}

object UserActions {

  case class UserLoginAction(task: FutureTask[Option[UserLoginState]]) extends TaskAction
  case class UserLoggedinAction(data: Option[UserLoginState]) extends Action
  
  case class UserLogoutAction(task: FutureTask[Unit]) extends TaskAction
  case class UserLoggedoutAction() extends Action
}
