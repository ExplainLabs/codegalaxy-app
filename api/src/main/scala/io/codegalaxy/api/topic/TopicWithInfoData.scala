package io.codegalaxy.api.topic

import play.api.libs.json._

case class TopicWithInfoData(alias: String,
                             name: String,
                             language: String,
                             info: TopicInfoData)

object TopicWithInfoData {

  implicit val jsonFormat: Format[TopicWithInfoData] = Json.format[TopicWithInfoData]
}
