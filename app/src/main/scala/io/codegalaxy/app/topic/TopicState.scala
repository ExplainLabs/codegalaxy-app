package io.codegalaxy.app.topic

import io.codegalaxy.app.topic.TopicActions._
import io.codegalaxy.domain.TopicEntity

case class TopicState(topics: Seq[TopicEntity] = Nil)

object TopicStateReducer {

  def apply(state: Option[TopicState], action: Any): TopicState = {
    reduce(state.getOrElse(TopicState()), action)
  }

  private def reduce(state: TopicState, action: Any): TopicState = action match {
    case TopicsFetchedAction(dataList) => state.copy(topics = dataList)
    case _ => state
  }
}
