package io.codegalaxy.api.data

import play.api.libs.json._

case class InfoData(numberOfQuestions: Int = 0,
                    numberOfPaid: Int = 0,
                    numberOfLearners: Int = 0,
                    numberOfChapters: Int = 0)

object InfoData {

  implicit val jsonReads: Reads[InfoData] = Json.reads[InfoData]
}
