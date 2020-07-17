package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.topic.TopicService._
import io.codegalaxy.domain.TopicEntity
import io.codegalaxy.domain.dao.TopicDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopicService(api: TopicApi, dao: TopicDao) {

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
            dataList <- api.getTopics(info = true)
            allData <- Future.sequence(dataList.map { data =>
              api.getTopicIcon(data.alias).map { icon =>
                (data, icon)
              }
            })
            res <- dao.upsertMany(allData.map { case (data, icon) =>
              convertToTopicEntity(data, icon)
            })
          } yield res
        }
    } yield res
  }
}

object TopicService {

  private def convertToTopicEntity(data: TopicWithInfoData,
                                   maybeIcon: Option[String]): TopicEntity = {
    TopicEntity(
      id = -1,
      alias = data.alias,
      name = data.name,
      lang = data.language,
      numQuestions = data.info.map(_.numberOfQuestions).getOrElse(0),
      numPaid = data.info.map(_.numberOfPaid).getOrElse(0),
      numLearners = data.info.map(_.numberOfLearners).getOrElse(0),
      numChapters = data.info.map(_.numberOfChapters).getOrElse(0),
      svgIcon = maybeIcon
    )
  }
}
