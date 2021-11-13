package io.codegalaxy.app.user

import common.reactnative.Linking
import io.codegalaxy.api.user._
import io.codegalaxy.app.config.ConfigService
import io.codegalaxy.app.user.UserActions._
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}
import scommons.reactnative.Image

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success
import scala.util.control.NonFatal

trait UserActions {

  protected def client: UserApi
  protected def userService: UserService
  protected def configService: ConfigService
  protected def image: Image

  def userLogin(dispatch: Dispatch, user: String, password: String): UserLoginAction = {
    val future = for {
      _ <- client.authenticate(user, password)
      res <- fetchUserData(refresh = true)
    } yield res
    
    val resultF = future.andThen {
      case Success((profile, config)) => dispatch(UserLoggedinAction(profile, config))
    }

    UserLoginAction(FutureTask("Authenticate User", resultF))
  }
  
  def userSignup(): UserSignupAction = {
    val resultF = Linking.openURL("https://codegalaxy.io/auth/signup")

    UserSignupAction(FutureTask("Signing up User", resultF))
  }
  
  def userProfileFetch(dispatch: Dispatch): UserLoginAction = {
    val resultF = fetchUserData(refresh = false).andThen {
      case Success((profile, config)) => dispatch(UserLoggedinAction(profile, config))
    }

    UserLoginAction(FutureTask("Fetching Profile", resultF))
  }
  
  private def fetchUserData(refresh: Boolean): Future[(Option[ProfileEntity], Option[ConfigEntity])] = {
    for {
      maybeProfile <- userService.fetchProfile(refresh)
      maybeConfig <- maybeProfile match {
        case None => Future.successful(None)
        case Some(profile) =>
          for {
            res <- configService.getConfig(profile.id)
            _ <- profile.avatarUrl match {
              case None => Future.successful(())
              case Some(avatarUrl) => image.prefetch(avatarUrl).recover {
                case NonFatal(ex) => println(s"Failed to prefetch profile image, url: $avatarUrl, error: $ex")
              }
            }
          } yield {
            res
          }
      }
    } yield {
      (maybeProfile, maybeConfig)
    }
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

  case class UserSignupAction(task: FutureTask[Unit]) extends TaskAction
  
  case class UserLoginAction(task: FutureTask[(Option[ProfileEntity], Option[ConfigEntity])]) extends TaskAction
  case class UserLoggedinAction(profile: Option[ProfileEntity], config: Option[ConfigEntity]) extends Action
  
  case class UserLogoutAction(task: FutureTask[Unit]) extends TaskAction
  case class UserLoggedoutAction() extends Action
}
