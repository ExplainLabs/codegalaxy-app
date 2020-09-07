package io.codegalaxy.api.data

import play.api.libs.json._

case class InfoData(numberOfQuestions: Int,
                    numberOfPaid: Int,
                    numberOfLearners: Int,
                    numberOfChapters: Int)

object InfoData {

  implicit val jsonReads: Reads[InfoData] = Json.reads[InfoData]
}
