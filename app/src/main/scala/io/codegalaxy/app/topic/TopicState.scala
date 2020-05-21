package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicActions._

case class TopicItemState(data: TopicWithInfoData, svgIcon: Option[String])

case class TopicState(topics: List[TopicItemState] = Nil)

object TopicStateReducer {

  def apply(state: Option[TopicState], action: Any): TopicState = {
    reduce(state.getOrElse(TopicState()), action)
  }

  private def reduce(state: TopicState, action: Any): TopicState = action match {
    case TopicsFetchedAction(dataList) => state.copy(topics = dataList)
    case _ => state
  }
}
