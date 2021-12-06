package io.codegalaxy.app.question

import io.codegalaxy.api.question.QuestionData
import scommons.react._
import scommons.react.hooks._

case class QuestionViewProps(data: QuestionData,
                             onSubmitAnswer: QuestionData => Unit,
                             onNextQuestion: () => Unit)

object QuestionView extends FunctionComponent[QuestionViewProps] {

  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText
  private[question] var questionChoicesComp: UiComponent[QuestionChoicesProps] = QuestionChoices
  private[question] var questionAnswerComp: UiComponent[QuestionAnswerProps] = QuestionAnswer
  private[question] var questionRuleComp: UiComponent[QuestionRuleProps] = QuestionRule
  private[question] var questionButtonComp: UiComponent[QuestionButtonProps] = QuestionButton
  
  protected def render(compProps: Props): ReactElement = {
    val (selectedIds, setSelectedIds) = useState(Set.empty[Int])
    val props = compProps.wrapped
    val question = props.data
    val multiSelect = question.answerType != "SINGLE_CHOICE"
    val answered = question.correct.isDefined
    
    <.>()(
      <(questionTextComp())(^.wrapped := QuestionTextProps(question.text))(),

      <(questionChoicesComp())(^.wrapped := QuestionChoicesProps(
        answered = answered,
        choices = question.choices,
        selectedIds = selectedIds,
        setSelectedIds = setSelectedIds,
        multiSelect = multiSelect
      ))(),

      question.correct.map { correct =>
        <(questionAnswerComp())(^.wrapped := QuestionAnswerProps(correct))()
      },

      question.rules.map { rule =>
        <(questionRuleComp())(^.wrapped := QuestionRuleProps(rule.title, rule.text))()
      },
      question.explanation.collect { case explanation if explanation.trim.nonEmpty =>
        <(questionRuleComp())(^.wrapped := QuestionRuleProps("Explanation", explanation))()
      },

      if (!answered) {
        <(questionButtonComp())(^.wrapped := QuestionButtonProps("Continue", onPress = { () =>
            val data = question.copy(choices = question.choices.map { choice =>
              val selected = selectedIds.contains(choice.id)
              choice.copy(selected = if (selected) Some(true) else Some(false))
            })
            props.onSubmitAnswer(data)
          }
        ))()
      }
      else {
        <(questionButtonComp())(^.wrapped := QuestionButtonProps("Next", onPress = { () =>
            props.onNextQuestion()
            setSelectedIds(Set.empty)
          }
        ))()
      }
    )
  }
}
