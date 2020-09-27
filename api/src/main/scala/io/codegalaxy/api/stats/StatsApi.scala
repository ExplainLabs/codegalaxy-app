package io.codegalaxy.api.stats

import scala.concurrent.Future

trait StatsApi {

  def getStats: Future[List[StatsRespData]]
  
  def getStatsTopic(topic: String): Future[StatsData]
  
  def getStatsChapter(topic: String, chapter: String): Future[StatsData]
}
