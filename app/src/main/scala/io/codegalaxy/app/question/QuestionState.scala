package io.codegalaxy.app.question

import io.codegalaxy.api.question.QuestionData
import io.codegalaxy.app.question.QuestionActions._

case class QuestionState(topic: Option[String] = None,
                         chapter: Option[String] = None,
                         question: Option[QuestionData] = None)

object QuestionStateReducer {

  def apply(state: Option[QuestionState], action: Any): QuestionState = {
    reduce(state.getOrElse(QuestionState()), action)
  }

  private def reduce(state: QuestionState, action: Any): QuestionState = action match {
    case QuestionFetchAction(t, c, _) if {
      !state.topic.contains(t) || !state.chapter.contains(c)
    } => state.copy(
      topic = Some(t),
      chapter = Some(c),
      question = None
    )
    case QuestionFetchedAction(topic, chapter, data) => state.copy(
      topic = Some(topic),
      chapter = Some(chapter),
      question = Some(data)
    )
    case _ => state
  }
}
