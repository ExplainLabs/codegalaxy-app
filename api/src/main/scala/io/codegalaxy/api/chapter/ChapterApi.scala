package io.codegalaxy.api.chapter

import scala.concurrent.Future

trait ChapterApi {

  def getChaptersWithStatistics(topic: String): Future[List[ChapterWithStatisticsRespData]]
}
