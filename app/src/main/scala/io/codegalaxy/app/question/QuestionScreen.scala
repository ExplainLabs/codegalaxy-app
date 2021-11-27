package io.codegalaxy.app.question

import io.codegalaxy.app.topic.TopicParams
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._

import scala.scalajs.js

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  private[question] var questionChoicesComp: UiComponent[QuestionChoicesProps] = QuestionChoices
  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText
  private[question] var questionButtonComp: UiComponent[QuestionButtonProps] = QuestionButton
  private[question] var questionAnswerComp: UiComponent[QuestionAnswerProps] = QuestionAnswer
  private[question] var questionRuleComp: UiComponent[QuestionRuleProps] = QuestionRule
  
  protected def render(compProps: Props): ReactElement = {
    implicit val theme: Theme = useTheme()
    val props = compProps.wrapped
    val topic = props.params.topic
    val chapter = props.params.getChapter
    val (selectedIds, setSelectedIds) = useState(Set.empty[Int])
    
    useEffect({ () =>
      if (!props.data.topic.contains(topic) || !props.data.chapter.contains(chapter)) {
        props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
      }
      ()
    }, Nil)

    <.SafeAreaView(
      ^.rnStyle := styles.container,
      ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
    )(props.data.question match {
      case None => <.Text(^.rnStyle := themeTextStyle)("Loading...")
      case Some(question) =>
        val multiSelect = question.answerType != "SINGLE_CHOICE"
        val answered = question.correct.isDefined
        
        <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
          <(questionTextComp())(^.wrapped := QuestionTextProps(question.text))(),

          <(questionChoicesComp())(^.wrapped := new QuestionChoicesProps(
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
                props.dispatch(props.actions.submitAnswer(props.dispatch, topic, chapter, data))
              }
            ))()
          }
          else {
            <(questionButtonComp())(^.wrapped := QuestionButtonProps("Next", onPress = { () =>
                props.dispatch(props.actions.fetchQuestion(props.dispatch, topic, chapter))
                setSelectedIds(Set.empty)
              }
            ))()
          }
        )
    })
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {

    val container: Style = new ViewStyle {
      override val flex = 1
      override val margin = 5
      override val padding = 5
      override val marginBottom = 0
      override val paddingBottom = 0
    }
  }
}
