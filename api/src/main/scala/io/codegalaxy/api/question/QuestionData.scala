package io.codegalaxy.api.question

import play.api.libs.json._

case class QuestionData(uuid: String,
                        text: String,
                        answerType: String,
                        choices: List[ChoiceData],
                        rules: List[RuleData] = Nil,
                        correct: Option[Boolean] = None,
                        explanation: Option[String] = None)

object QuestionData {

  implicit val jsonFormat: Format[QuestionData] = Json.format[QuestionData]
}
