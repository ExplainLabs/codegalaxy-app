package io.codegalaxy.app.auth

import io.codegalaxy.api.auth._
import io.codegalaxy.app.auth.AuthActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait AuthActions {

  protected def client: AuthApi

  def authenticate(dispatch: Dispatch, user: String, password: String): AuthAuthenticateAction = {
    val future = client.authenticate(user, password).andThen {
      case Success(cgidToken) =>
        dispatch(AuthAuthenticatedAction(cgidToken))
    }

    AuthAuthenticateAction(FutureTask("Authenticate", future))
  }
}

object AuthActions {

  case class AuthAuthenticateAction(task: FutureTask[String]) extends TaskAction
  case class AuthAuthenticatedAction(cgidToken: String) extends Action
}
