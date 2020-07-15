package io.codegalaxy.app.topic

import io.codegalaxy.domain.TopicEntity
import io.codegalaxy.domain.dao.TopicDao

import scala.concurrent.Future

class TopicService(dao: TopicDao) {

  def getById(id: Int): Future[Option[TopicEntity]] = {
    dao.getById(id)
  }
  
  def list(): Future[Seq[TopicEntity]] = {
    dao.list()
  }
  
  def save(data: Seq[TopicEntity]): Future[Seq[TopicEntity]] = {
    dao.upsertMany(data)
  }
}
