package io.codegalaxy.app.chapter

import io.codegalaxy.api.chapter._
import io.codegalaxy.app.chapter.ChapterService._
import io.codegalaxy.domain.ChapterEntity
import io.codegalaxy.domain.dao.ChapterDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChapterService(api: ChapterApi, dao: ChapterDao) {

  def getByAlias(topic: String, alias: String): Future[Option[ChapterEntity]] = {
    dao.getByAlias(topic, alias)
  }
  
  def fetch(topic: String, refresh: Boolean = false): Future[Seq[ChapterEntity]] = {
    for {
      existing <-
        if (refresh) Future.successful(Nil)
        else dao.list(topic)
      res <-
        if (existing.nonEmpty) Future.successful(existing)
        else {
          for {
            dataList <- getChaptersData(topic)
            res <- dao.upsertMany(topic, dataList.map { case (data, progress) =>
              convertToChapterEntity(topic, data, progress)
            })
          } yield res
        }
    } yield res
  }
  
  private def getChaptersData(topic: String): Future[List[(ChapterData, Int)]] = {
    for {
      dataList <- api.getChapters(topic)
    } yield {
      dataList.map { data =>
        (data.chapter, data.stats.progressAll)
      }
    }
  }
}

object ChapterService {

  private def convertToChapterEntity(topic: String,
                                     data: ChapterData,
                                     progress: Int): ChapterEntity = {
    ChapterEntity(
      id = -1,
      topic = topic,
      alias = data.alias,
      name = data.name,
      numQuestions = data.info.numberOfQuestions,
      numPaid = data.info.numberOfPaid,
      numLearners = data.info.numberOfLearners,
      numChapters = data.info.numberOfChapters,
      progress = progress
    )
  }
}
