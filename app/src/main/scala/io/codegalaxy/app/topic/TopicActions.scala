package io.codegalaxy.app.topic

import io.codegalaxy.app.topic.TopicActions._
import io.codegalaxy.domain.Topic
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global

trait TopicActions {

  protected def topicService: TopicService

  def fetchTopics(dispatch: Dispatch, refresh: Boolean = false): TopicsFetchAction = {
    val resultF = topicService.fetch(refresh).map { dataList =>
      dispatch(TopicsFetchedAction(dataList))
      dataList
    }

    TopicsFetchAction(FutureTask("Fetching Topics", resultF))
  }
}

object TopicActions {

  case class TopicsFetchAction(task: FutureTask[Seq[Topic]]) extends TaskAction
  case class TopicsFetchedAction(dataList: Seq[Topic]) extends Action
}
