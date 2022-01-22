package io.codegalaxy.app.topic

import io.codegalaxy.app.topic.TopicActions.TopicsFetchAction
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockTopicActions(
  fetchTopicsMock: (Dispatch, Boolean) => TopicsFetchAction = (_, _) => ???
) extends TopicActions {
  
  override protected def topicService: TopicService = ???

  override def fetchTopics(dispatch: Dispatch, refresh: Boolean): TopicsFetchAction =
    fetchTopicsMock(dispatch, refresh)
}
