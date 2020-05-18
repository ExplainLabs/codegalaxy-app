package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait TopicActions {

  protected def client: TopicApi

  def fetchTopics(dispatch: Dispatch): TopicsFetchAction = {
    val resultF = client.getTopics(info = true).andThen {
      case Success(dataList) => dispatch(TopicsFetchedAction(dataList))
    }

    TopicsFetchAction(FutureTask("Fetching Topics", resultF))
  }
}

object TopicActions {

  case class TopicsFetchAction(task: FutureTask[List[TopicWithInfoData]]) extends TaskAction
  case class TopicsFetchedAction(dataList: List[TopicWithInfoData]) extends Action
}
