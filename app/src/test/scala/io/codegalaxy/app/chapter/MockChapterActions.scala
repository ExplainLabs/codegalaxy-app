package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions.ChaptersFetchAction
import scommons.react.redux.Dispatch

//noinspection NotImplementedCode
class MockChapterActions(
  fetchChaptersMock: (Dispatch, String, Boolean) => ChaptersFetchAction = (_, _, _) => ???
) extends ChapterActions {

  override protected def chapterService: ChapterService = ???
  
  override def fetchChapters(dispatch: Dispatch,
                             topic: String,
                             refresh: Boolean): ChaptersFetchAction = {

    fetchChaptersMock(dispatch, topic, refresh)
  }
}
