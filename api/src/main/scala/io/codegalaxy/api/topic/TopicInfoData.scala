package io.codegalaxy.api.topic

import play.api.libs.json._

case class TopicInfoData(numberOfQuestions: Int,
                         numberOfPaid: Int,
                         numberOfLearners: Int,
                         numberOfChapters: Int)

object TopicInfoData {

  implicit val jsonFormat: Format[TopicInfoData] = Json.format[TopicInfoData]
}
