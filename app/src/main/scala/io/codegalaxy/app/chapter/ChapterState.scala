package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.domain.ChapterEntity

case class ChapterState(topic: Option[String] = None,
                        chapters: Seq[ChapterEntity] = Nil)

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
    case _ => state
  }
}
