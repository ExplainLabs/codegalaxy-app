package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.domain.Chapter
import scommons.react.redux._
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global

trait ChapterActions {

  protected def chapterService: ChapterService

  def fetchChapters(dispatch: Dispatch,
                    topic: String,
                    refresh: Boolean = false): ChaptersFetchAction = {
    
    val resultF = chapterService.fetch(topic, refresh).map { dataList =>
      dispatch(ChaptersFetchedAction(topic, dataList))
      dataList
    }

    ChaptersFetchAction(topic, FutureTask(s"Fetching $topic chapters", resultF))
  }
}

object ChapterActions {

  case class ChaptersFetchAction(topic: String, task: FutureTask[Seq[Chapter]]) extends TaskAction
  case class ChaptersFetchedAction(topic: String, dataList: Seq[Chapter]) extends Action
}
