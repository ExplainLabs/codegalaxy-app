package io.codegalaxy.api.topic

import io.codegalaxy.api.data.InfoData
import play.api.libs.json._

case class TopicWithInfoData(alias: String,
                             name: String,
                             language: String,
                             info: InfoData)

object TopicWithInfoData {

  implicit val jsonReads: Reads[TopicWithInfoData] = Json.reads[TopicWithInfoData]
}
