package io.codegalaxy.app.topic

import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.app.stats.MockStatsApi

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockTopicWithStatsApi(
  getStatsMock: () => Future[List[StatsRespData]] = () => ???,
  getStatsTopicMock: String => Future[StatsData] = _ => ???,
  getStatsChapterMock: (String, String) => Future[StatsData] = (_, _) => ???,
  getTopicsMock: () => Future[List[TopicWithInfoData]] = () => ???,
  getTopicIconMock: String => Future[Option[String]] = _ => ???
) extends
  MockStatsApi(
    getStatsMock,
    getStatsTopicMock,
    getStatsChapterMock
  ) with TopicApi {

  override def getTopics: Future[List[TopicWithInfoData]] = getTopicsMock()

  override def getTopicIcon(alias: String): Future[Option[String]] = getTopicIconMock(alias)
}
