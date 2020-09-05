package io.codegalaxy.api.stats

import play.api.libs.json._

case class StatsData(progressAll: Int)

object StatsData {

  implicit val jsonReads: Reads[StatsData] = Json.reads[StatsData]
}
