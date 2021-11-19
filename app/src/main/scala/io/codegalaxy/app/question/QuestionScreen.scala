package io.codegalaxy.app.question

import io.codegalaxy.api.question.ChoiceData
import io.codegalaxy.app.topic.TopicParams
import scommons.react._
import scommons.react.hooks._
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._
import scommons.reactnative.ui._

import scala.scalajs.js

case class QuestionScreenProps(dispatch: Dispatch,
                               actions: QuestionActions,
                               data: QuestionState,
                               params: TopicParams)

object QuestionScreen extends FunctionComponent[QuestionScreenProps] {

  private[question] var choiceGroupComp: UiComponent[ChoiceGroupProps[Int, ChoiceData]] =
    new ChoiceGroup[Int, ChoiceData]
  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText
  private[question] var questionButtonComp: UiComponent[QuestionButtonProps] = QuestionButton
  private[question] var questionAnswerComp: UiComponent[QuestionAnswerProps] = QuestionAnswer
  private[question] var questionAnswerIcon: UiComponent[QuestionAnswerIconProps] = QuestionAnswerIcon
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

          <(choiceGroupComp())(^.wrapped := new ChoiceGroupProps[Int, ChoiceData](
            items = question.choices,
            keyExtractor = _.id,
            iconRenderer = {
              if (!answered) ChoiceGroupProps.defaultIconRenderer(multiSelect)
              else { (_, _) => null }
            },
            labelRenderer = { (data, _) =>
              val selected = selectedIds.contains(data.id)
              <.>()(
                if (answered) Some {
                  <(questionAnswerIcon())(^.wrapped := QuestionAnswerIconProps(
                    correct = data.correct.getOrElse(false)
                  ))()
                }
                else None,
                <(questionTextComp())(^.wrapped := QuestionTextProps(
                  textHtml = data.choiceText,
                  style = Some(
                    if (answered && !selected) js.Array(styles.choiceLabel, styles.choiceNotSelected)
                    else js.Array(styles.choiceLabel)
                  )
                ))()
              )
            },
            selectedIds = selectedIds,
            onSelectChange = { ids =>
              if (!answered) {
                setSelectedIds(ids)
              }
            },
            multiSelect = multiSelect,
            style = Some(styles.choiceGroup)
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
    import ViewStyle._

    val container: Style = new ViewStyle {
      override val flex = 1
      override val margin = 5
      override val padding = 5
      override val marginBottom = 0
      override val paddingBottom = 0
    }
    val choiceGroup: Style = new ViewStyle {
      override val alignSelf = AlignSelf.center
      override val margin = 5
      override val paddingVertical = 5
      override val paddingLeft = 5
      override val paddingRight = 25
    }
    val choiceLabel: Style = new ViewStyle {
      override val marginHorizontal = 5
      override val paddingHorizontal = 5
      override val borderBottomWidth = 1
      override val borderBottomColor = Style.Color.gray
    }
    val choiceNotSelected = new ViewStyle {
      val opacity = 0.5
    }
  }
}
