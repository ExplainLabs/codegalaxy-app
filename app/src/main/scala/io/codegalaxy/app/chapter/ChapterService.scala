package io.codegalaxy.app.chapter

import io.codegalaxy.api.chapter._
import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats.StatsData
import io.codegalaxy.app.chapter.ChapterService._
import io.codegalaxy.app.stats.StatsService
import io.codegalaxy.domain.dao.ChapterDao
import io.codegalaxy.domain.{Chapter, ChapterEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChapterService(api: ChapterApi, dao: ChapterDao) {

  def getByAlias(topic: String, alias: String): Future[Option[Chapter]] = {
    dao.getByAlias(topic, alias)
  }
  
  def fetch(topic: String, refresh: Boolean = false): Future[Seq[Chapter]] = {
    for {
      existing <-
        if (refresh) Future.successful(Nil)
        else dao.list(topic)
      res <-
        if (existing.nonEmpty) Future.successful(existing)
        else {
          for {
            dataList <- getChaptersData(topic)
            res <- dao.saveAll(topic, dataList.map { case (data, stats) =>
              convertToChapter(topic, data, stats)
            })
          } yield res
        }
    } yield res
  }
  
  private def getChaptersData(topic: String): Future[List[(ChapterData, StatsData)]] = {
    for {
      dataList <- api.getChapters(topic)
    } yield {
      dataList.map { data =>
        (data.chapter, data.stats)
      }
    }
  }
}

object ChapterService {

  private def convertToChapter(topic: String,
                               data: ChapterData,
                               stats: StatsData): Chapter = {
    Chapter(
      entity = convertToChapterEntity(topic, data),
      stats = Some(StatsService.convertToChapterStats(stats))
    )
  }
  
  private def convertToChapterEntity(topic: String, data: ChapterData): ChapterEntity = {
    val info = data.info.getOrElse(InfoData())
    
    ChapterEntity(
      id = -1,
      topic = topic,
      alias = data.alias,
      name = data.name,
      numQuestions = info.numberOfQuestions,
      numPaid = info.numberOfPaid,
      numLearners = info.numberOfLearners,
      numChapters = info.numberOfChapters
    )
  }
}
