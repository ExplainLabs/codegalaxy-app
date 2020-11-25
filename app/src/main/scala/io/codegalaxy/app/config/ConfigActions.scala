package io.codegalaxy.app.config

import io.codegalaxy.app.config.ConfigActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConfigActions {

  def updateConfig(dispatch: Dispatch, userId: Int, darkTheme: Boolean): ConfigUpdateAction = {
    //TODO: save to local DB
    val resultF = Future.successful(darkTheme).andThen {
      case _ => dispatch(ConfigUpdatedAction(darkTheme))
    }

    ConfigUpdateAction(FutureTask("Updating Config", resultF))
  }
}

object ConfigActions {

  case class ConfigUpdateAction(task: FutureTask[Boolean]) extends TaskAction
  case class ConfigUpdatedAction(darkTheme: Boolean) extends Action
}
