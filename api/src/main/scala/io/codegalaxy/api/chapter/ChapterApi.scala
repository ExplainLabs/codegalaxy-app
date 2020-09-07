package io.codegalaxy.api.chapter

import scala.concurrent.Future

trait ChapterApi {

  def getChapters(topic: String): Future[List[ChapterRespData]]
}
