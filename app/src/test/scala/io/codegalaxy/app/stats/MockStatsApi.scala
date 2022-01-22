package io.codegalaxy.app.stats

import io.codegalaxy.api.stats._

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockStatsApi(
  getStatsMock: () => Future[List[StatsRespData]] = () => ???,
  getStatsTopicMock: String => Future[StatsData] = _ => ???,
  getStatsChapterMock: (String, String) => Future[StatsData] = (_, _) => ???
) extends StatsApi {

  override def getStats: Future[List[StatsRespData]] =
    getStatsMock()

  override def getStatsTopic(topic: String): Future[StatsData] =
    getStatsTopicMock(topic)

  override def getStatsChapter(topic: String, chapter: String): Future[StatsData] =
    getStatsChapterMock(topic, chapter)
}
