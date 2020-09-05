package io.codegalaxy.app.topic

import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicService._
import io.codegalaxy.domain.TopicEntity
import io.codegalaxy.domain.dao.TopicDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopicService(api: TopicApi with StatsApi,
                   dao: TopicDao) {

  def getById(id: Int): Future[Option[TopicEntity]] = {
    dao.getById(id)
  }
  
  def fetch(refresh: Boolean = false): Future[Seq[TopicEntity]] = {
    for {
      existing <-
        if (refresh) Future.successful(Nil)
        else dao.list()
      res <-
        if (existing.nonEmpty) Future.successful(existing)
        else {
          for {
            dataList <- getTopicsData
            res <- dao.upsertMany(dataList.map {
              case (data, icon, progress) =>
                convertToTopicEntity(data, icon, progress)
            })
          } yield res
        }
    } yield res
  }
  
  private def getTopicsData: Future[List[(TopicWithInfoData, Option[String], Option[Int])]] = {
    for {
      dataList <- api.getTopics
      dataAndIconList <- Future.sequence(dataList.map { data =>
        api.getTopicIcon(data.alias).map { icon =>
          (data, icon)
        }
      })
      statsList <- api.getStats
    } yield {
      dataAndIconList.map { case (data, icon) =>
        val maybeProgress = statsList
          .find(_.topic.alias == data.alias)
          .map(_.statistics.progressAll)
        
        (data, icon, maybeProgress)
      }
    }
  }
}

object TopicService {

  private def convertToTopicEntity(data: TopicWithInfoData,
                                   maybeIcon: Option[String],
                                   maybeProgress: Option[Int]): TopicEntity = {
    TopicEntity(
      id = -1,
      alias = data.alias,
      name = data.name,
      lang = data.language,
      numQuestions = data.info.numberOfQuestions,
      numPaid = data.info.numberOfPaid,
      numLearners = data.info.numberOfLearners,
      numChapters = data.info.numberOfChapters,
      svgIcon = maybeIcon,
      progress = maybeProgress
    )
  }
}
