package io.codegalaxy.app.topic

import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.app.topic.TopicActions._
import io.codegalaxy.domain.Topic

case class TopicState(topics: Seq[Topic] = Nil)

object TopicStateReducer {

  def apply(state: Option[TopicState], action: Any): TopicState = {
    reduce(state.getOrElse(TopicState()), action)
  }

  private def reduce(state: TopicState, action: Any): TopicState = action match {
    case TopicsFetchedAction(dataList) => state.copy(topics = dataList)
    case StatsFetchedAction((stats, _)) => state.copy(topics = state.topics.map {
      case t if t.entity.id == stats.id => t.copy(stats = Some(stats))
      case t => t
    })
    case _ => state
  }
}
