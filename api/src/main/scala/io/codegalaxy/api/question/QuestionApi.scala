package io.codegalaxy.api.question

import scala.concurrent.Future

trait QuestionApi {

  def getNextQuestion(topic: String, chapter: String): Future[QuestionData]
  
  def submitAnswer(topic: String, chapter: String, data: QuestionData): Future[QuestionData]
}
