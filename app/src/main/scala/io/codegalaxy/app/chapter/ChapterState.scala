package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.domain.Chapter

case class ChapterState(topic: Option[String] = None,
                        chapters: Seq[Chapter] = Nil)

object ChapterStateReducer {

  def apply(state: Option[ChapterState], action: Any): ChapterState = {
    reduce(state.getOrElse(ChapterState()), action)
  }

  private def reduce(state: ChapterState, action: Any): ChapterState = action match {
    case ChaptersFetchAction(topic, _) => state.copy(
      topic = Some(topic),
      chapters = Nil
    )
    case ChaptersFetchedAction(topic, dataList) => state.copy(
      topic = Some(topic),
      chapters = dataList
    )
    case StatsFetchedAction((_, stats)) => state.copy(chapters = state.chapters.map {
      case t if t.entity.id == stats.id => t.copy(stats = Some(stats))
      case t => t
    })
    case _ => state
  }
}
