package io.codegalaxy.app.question

import io.codegalaxy.api.question.{QuestionApi, QuestionData}

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockQuestionApi(
  getNextQuestionMock: (String, String) => Future[QuestionData] = (_, _) => ???,
  submitAnswerMock: (String, String, QuestionData) => Future[QuestionData] = (_, _, _) => ???
) extends QuestionApi {

  override def getNextQuestion(topic: String, chapter: String): Future[QuestionData] =
    getNextQuestionMock(topic, chapter)

  override def submitAnswer(topic: String, chapter: String, data: QuestionData): Future[QuestionData] =
    submitAnswerMock(topic, chapter, data)
}
