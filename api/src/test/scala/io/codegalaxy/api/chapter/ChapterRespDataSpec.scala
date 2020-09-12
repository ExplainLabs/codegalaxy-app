package io.codegalaxy.api.chapter

import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats.StatsData
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class ChapterRespDataSpec extends FlatSpec with Matchers {

  private val dataList = List(ChapterRespData(
    chapter = ChapterData(
      alias = "test_chapter",
      name = "Test Chapter Name",
      info = Some(InfoData(
        numberOfQuestions = 1,
        numberOfPaid = 2,
        numberOfLearners = 3,
        numberOfChapters = 4
      ))
    ),
    stats = StatsData(
      progressAll = 100
    )
  ))

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""[{
       |  "chapter": {
       |    "alias": "test_chapter",
       |    "name": "Test Chapter Name",
       |    "info": {
       |      "numberOfQuestions": 1,
       |      "numberOfPaid": 2,
       |      "numberOfLearners": 3,
       |      "numberOfChapters": 4
       |    }
       |  },
       |  "stats": {
       |    "progress": 10,
       |    "progressOnce": 20,
       |    "progressAll": 100,
       |    "freePercent": 30,
       |    "paid": 40,
       |    "numberOfAnswers": 41,
       |    "lastAnswerTimestamp": 1581848327229,
       |    "daysOnStreak": {
       |      "daysOnStreak": 123
       |    }
       |  }
       |}]""".stripMargin
  ))

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[List[ChapterRespData]] shouldBe dataList
  }
}
