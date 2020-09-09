package io.codegalaxy.api.question

import play.api.libs.json._

case class ChoiceData(id: Int,
                      choiceText: String,
                      correct: Option[Boolean] = None,
                      selected: Option[Boolean] = None)

object ChoiceData {

  implicit val jsonFormat: Format[ChoiceData] = Json.format[ChoiceData]
}
