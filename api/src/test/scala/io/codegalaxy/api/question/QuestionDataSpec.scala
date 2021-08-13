package io.codegalaxy.api.question

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class QuestionDataSpec extends AnyFlatSpec with Matchers {

  private val data = QuestionData(
    uuid = "14h15kl1h514l5h4315j145lj1",
    text = "Can methods, taking one argument, be used with infix syntax?",
    answerType = "SINGLE_CHOICE",
    choices = List(
      ChoiceData(
        id = 1,
        choiceText = "Yes",
        correct = Some(true),
        selected = Some(false)
      ),
      ChoiceData(
        id = 2,
        choiceText = "No",
        correct = Some(false),
        selected = Some(true)
      )
    ),
    rules = List(RuleData(
      text = "<b>Arity-1</b><br>Scala has a special syntax for invoking methods of arity-1 (one argument)",
      title = "Arity-1"
    )),
    correct = Some(true),
    explanation = Some("test explanation")
  )

  private val expectedJson = Json.prettyPrint(Json.parse(
    s"""{
       |  "uuid": "14h15kl1h514l5h4315j145lj1",
       |  "text": "Can methods, taking one argument, be used with infix syntax?",
       |  "answerType": "SINGLE_CHOICE",
       |  "choices": [{
       |    "id": 1,
       |    "choiceText": "Yes",
       |    "correct": true,
       |    "selected": false
       |  }, {
       |    "id": 2,
       |    "choiceText": "No",
       |    "correct": false,
       |    "selected": true
       |  }],
       |  "rules": [{
       |    "text": "<b>Arity-1</b><br>Scala has a special syntax for invoking methods of arity-1 (one argument)",
       |    "title": "Arity-1"
       |  }],
       |  "correct": true,
       |  "explanation": "test explanation"
       |}""".stripMargin
  ))

  it should "serialize data to json" in {
    //when & then
    Json.prettyPrint(Json.parse(Json.stringify(Json.toJson(data)))) shouldBe expectedJson
  }

  it should "deserialize data from json" in {
    //when & then
    Json.parse(expectedJson).as[QuestionData] shouldBe data
  }
}
