package io.codegalaxy.api.question

import play.api.libs.json._

case class RuleData(text: String, title: String)

object RuleData {

  implicit val jsonFormat: Format[RuleData] = Json.format[RuleData]
}
