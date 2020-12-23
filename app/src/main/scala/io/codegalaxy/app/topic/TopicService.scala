package io.codegalaxy.app.topic

import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.app.stats.StatsService
import io.codegalaxy.app.topic.TopicService._
import io.codegalaxy.domain.dao.TopicDao
import io.codegalaxy.domain.{Topic, TopicEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopicService(api: TopicApi with StatsApi,
                   dao: TopicDao) {

  def getByAlias(alias: String): Future[Option[Topic]] = {
    dao.getByAlias(alias)
  }
  
  def fetch(refresh: Boolean = false): Future[Seq[Topic]] = {
    for {
      existing <-
        if (refresh) Future.successful(Nil)
        else dao.list()
      res <-
        if (existing.nonEmpty) Future.successful(existing)
        else {
          for {
            dataList <- getTopicsData
            res <- dao.saveAll(dataList.map { case (data, icon, stats) =>
                convertToTopic(data, icon, stats)
            })
          } yield res
        }
    } yield res
  }
  
  private def getTopicsData: Future[List[(TopicWithInfoData, Option[String], Option[StatsData])]] = {
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
        val maybeStats = statsList
          .find(_.topic.alias == data.alias)
          .map(_.statistics)
        
        (data, icon, maybeStats)
      }
    }
  }
}

object TopicService {

  private def convertToTopic(data: TopicWithInfoData,
                             maybeIcon: Option[String],
                             stats: Option[StatsData]): Topic = {
    Topic(
      entity = convertToTopicEntity(data, maybeIcon),
      stats = stats.map(StatsService.convertToTopicStats)
    )
  }
  
  private def convertToTopicEntity(data: TopicWithInfoData, maybeIcon: Option[String]): TopicEntity = {
    TopicEntity(
      id = -1,
      alias = data.alias,
      name = data.name,
      lang = data.language,
      numQuestions = data.info.numberOfQuestions,
      numPaid = data.info.numberOfPaid,
      numLearners = data.info.numberOfLearners,
      numChapters = data.info.numberOfChapters,
      numTheory = data.info.numberOfTheory,
      svgIcon = maybeIcon
    )
  }
}
