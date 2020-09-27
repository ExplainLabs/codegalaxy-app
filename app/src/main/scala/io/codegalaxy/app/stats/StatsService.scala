package io.codegalaxy.app.stats

import io.codegalaxy.api.stats._
import io.codegalaxy.app.stats.StatsService._
import io.codegalaxy.domain.dao.{ChapterDao, TopicDao}
import io.codegalaxy.domain.{ChapterStats, TopicStats}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StatsService(api: StatsApi, topicDao: TopicDao, chapterDao: ChapterDao) {

  def updateStats(topic: String, chapter: String): Future[(TopicStats, ChapterStats)] = {
    val topicStatsF = updateTopicStats(topic)
    val chapterStatsF = updateChapterStats(topic, chapter)
    for {
      topicStats <- topicStatsF
      chapterStats <- chapterStatsF
    } yield (topicStats, chapterStats)
  }
  
  private def updateTopicStats(topic: String): Future[TopicStats] = {
    for {
      stats <- api.getStatsTopic(topic)
      res <- topicDao.saveStats(topic, convertToTopicStats(stats))
    } yield {
      res.getOrElse {
        throw new IllegalStateException(s"TopicEntity not found: $topic")
      }
    }
  }
  
  private def updateChapterStats(topic: String, chapter: String): Future[ChapterStats] = {
    for {
      stats <- api.getStatsChapter(topic, chapter)
      res <- chapterDao.saveStats(topic, chapter, convertToChapterStats(stats))
    } yield {
      res.getOrElse {
        throw new IllegalStateException(s"ChapterEntity not found: $topic/$chapter")
      }
    }
  }
}

object StatsService {

  def convertToTopicStats(stats: StatsData): TopicStats = {
    TopicStats(
      id = -1,
      progress = stats.progress,
      progressOnce = stats.progressOnce,
      progressAll = stats.progressAll,
      freePercent = stats.freePercent,
      paid = stats.paid
    )
  }

  def convertToChapterStats(stats: StatsData): ChapterStats = {
    ChapterStats(
      id = -1,
      progress = stats.progress,
      progressOnce = stats.progressOnce,
      progressAll = stats.progressAll,
      freePercent = stats.freePercent,
      paid = stats.paid
    )
  }
}
