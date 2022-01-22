package io.codegalaxy.app.chapter

import io.codegalaxy.domain.Chapter

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockChapterService(
  fetchMock: (String, Boolean) => Future[Seq[Chapter]] = (_, _) => ???
) extends ChapterService(null, null) {

  override def fetch(topic: String, refresh: Boolean): Future[Seq[Chapter]] =
    fetchMock(topic, refresh)
}
