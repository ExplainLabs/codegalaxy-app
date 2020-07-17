package io.codegalaxy.api.topic

import scala.concurrent.Future

trait TopicApi {

  def getTopics: Future[List[TopicWithInfoData]]

  def getTopicIcon(alias: String): Future[Option[String]]
}
