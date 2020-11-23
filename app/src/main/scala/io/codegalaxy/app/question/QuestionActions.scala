package io.codegalaxy.app.question

import io.codegalaxy.api.question.{QuestionApi, QuestionData}
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.stats.StatsActions.StatsFetchedAction
import io.codegalaxy.app.stats.StatsService
import io.github.shogowada.scalajs.reactjs.redux.Action
import io.github.shogowada.scalajs.reactjs.redux.Redux.Dispatch
import scommons.react.redux.task.{FutureTask, TaskAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait QuestionActions {

  protected def client: QuestionApi
  protected def statsService: StatsService

  def fetchQuestion(dispatch: Dispatch, topic: String, chapter: String): QuestionFetchAction = {
    val resultF = client.getNextQuestion(topic, chapter).andThen {
      case Success(data) => dispatch(QuestionFetchedAction(topic, chapter, data))
    }

    QuestionFetchAction(topic, chapter, FutureTask("Fetching Question", resultF))
  }
  
  def submitAnswer(dispatch: Dispatch, topic: String, chapter: String, data: QuestionData): AnswerSubmitAction = {
    val resultF = for {
      next <- client.submitAnswer(topic, chapter, data)
      stats <- statsService.updateStats(topic, chapter)
    } yield {
      dispatch(QuestionFetchedAction(topic, chapter, next))
      dispatch(StatsFetchedAction(stats))
      next
    }

    AnswerSubmitAction(FutureTask("Submitting Answer", resultF))
  }
}

object QuestionActions {

  case class QuestionFetchAction(topic: String, chapter: String, task: FutureTask[QuestionData]) extends TaskAction
  case class QuestionFetchedAction(topic: String, chapter: String, data: QuestionData) extends Action
  
  case class AnswerSubmitAction(task: FutureTask[QuestionData]) extends TaskAction
}
