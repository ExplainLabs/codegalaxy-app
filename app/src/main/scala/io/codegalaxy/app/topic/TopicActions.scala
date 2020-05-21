package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicActions._
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TopicActions {

  protected def client: TopicApi

  def fetchTopics(dispatch: Dispatch): TopicsFetchAction = {
    val resultF = for {
      dataList <- client.getTopics(info = true)
      res <- Future.sequence(dataList.map { data =>
        client.getTopicIcon(data.alias).map { icon =>
          TopicItemState(data, icon)
        }
      })
    } yield {
      dispatch(TopicsFetchedAction(res))
      res
    }

    TopicsFetchAction(FutureTask("Fetching Topics", resultF))
  }
}

object TopicActions {

  case class TopicsFetchAction(task: FutureTask[List[TopicItemState]]) extends TaskAction
  case class TopicsFetchedAction(dataList: List[TopicItemState]) extends Action
}
