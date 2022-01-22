package io.codegalaxy.app.chapter

import io.codegalaxy.api.chapter._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockChapterApi(
  getChaptersWithStatisticsMock: String => Future[List[ChapterWithStatisticsRespData]] = _ => ???
) extends ChapterApi {
  
  override def getChaptersWithStatistics(topic: String): Future[List[ChapterWithStatisticsRespData]] =
    getChaptersWithStatisticsMock(topic)
}
