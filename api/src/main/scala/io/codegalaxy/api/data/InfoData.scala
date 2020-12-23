package io.codegalaxy.api.data

import play.api.libs.json._

case class InfoData(numberOfQuestions: Int = 0,
                    numberOfPaid: Int = 0,
                    numberOfLearners: Int = 0,
                    numberOfChapters: Int = 0,
                    numberOfTheory: Option[Int] = Option.empty)

object InfoData {

  implicit val jsonReads: Reads[InfoData] = Json.reads[InfoData]
}
