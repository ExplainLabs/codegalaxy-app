package io.codegalaxy.api.stats

import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class StatsRespDataSpec extends FlatSpec with Matchers {

  private val dataList = List(StatsRespData(
    topic = StatsTopicData(
      alias = "test_topic"
    ),
    statistics = StatsData(
      progress = 10,
      progressOnce = 20,
      progressAll = 100,
      freePercent = 30,
      paid = 40
    )
  ))

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""[{
       |  "topic" : {
       |    "alias" : "test_topic"
       |  },
       |  "statistics" : {
       |    "progress": 10,
       |    "progressOnce": 20,
       |    "progressAll": 100,
       |    "freePercent": 30,
       |    "paid": 40
       |  }
       |}]""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[List[StatsRespData]] shouldBe dataList
  }
}
