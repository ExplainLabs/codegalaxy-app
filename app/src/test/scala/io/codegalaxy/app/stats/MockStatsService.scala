package io.codegalaxy.app.stats

import io.codegalaxy.domain.{ChapterStats, TopicStats}

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockStatsService(
  updateStatsMock: (String, String) => Future[(TopicStats, ChapterStats)] = (_, _) => ???
) extends StatsService(null, null, null) {

  override def updateStats(topic: String, chapter: String): Future[(TopicStats, ChapterStats)] =
    updateStatsMock(topic, chapter)
}
