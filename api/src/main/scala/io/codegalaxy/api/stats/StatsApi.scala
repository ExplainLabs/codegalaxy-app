package io.codegalaxy.api.stats

import scala.concurrent.Future

trait StatsApi {

  def getStats: Future[List[StatsRespData]]
}
