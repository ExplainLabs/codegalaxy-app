package io.codegalaxy.api.chapter

import io.codegalaxy.api.data.InfoData
import play.api.libs.json._

case class ChapterData(alias: String,
                       name: String,
                       info: Option[InfoData])

object ChapterData {

  implicit val jsonReads: Reads[ChapterData] = Json.reads[ChapterData]
}
