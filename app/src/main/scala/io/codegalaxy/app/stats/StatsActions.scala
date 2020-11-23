package io.codegalaxy.app.stats

import io.codegalaxy.domain.{ChapterStats, TopicStats}
import io.github.shogowada.scalajs.reactjs.redux.Action

object StatsActions {

  case class StatsFetchedAction(stats: (TopicStats, ChapterStats)) extends Action
}
