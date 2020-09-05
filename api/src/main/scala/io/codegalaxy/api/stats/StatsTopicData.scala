package io.codegalaxy.api.stats

import play.api.libs.json._

case class StatsTopicData(alias: String)

object StatsTopicData {

  implicit val jsonReads: Reads[StatsTopicData] = Json.reads[StatsTopicData]
}
