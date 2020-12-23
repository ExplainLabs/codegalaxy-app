package io.codegalaxy.api.chapter

import play.api.libs.json.{Json, Reads}

case class ChapterRespData(chapter: ChapterData)

object ChapterRespData {

  implicit val jsonReads: Reads[ChapterRespData] = Json.reads[ChapterRespData]
}
