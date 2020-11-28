package io.codegalaxy.app.config

import io.codegalaxy.app.config.ConfigActions._
import io.codegalaxy.domain.ConfigEntity
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait ConfigActions {

  protected def configService: ConfigService

  def updateDarkTheme(dispatch: Dispatch, userId: Int, darkTheme: Boolean): ConfigUpdateAction = {
    val resultF = configService.setDarkTheme(userId, darkTheme).andThen {
      case Success(config) => dispatch(ConfigUpdatedAction(config))
    }

    ConfigUpdateAction(FutureTask("Updating darkTheme Config", resultF))
  }
}

object ConfigActions {

  case class ConfigUpdateAction(task: FutureTask[ConfigEntity]) extends TaskAction
  case class ConfigUpdatedAction(config: ConfigEntity) extends Action
}
