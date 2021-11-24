package io.codegalaxy.app.question

import io.codegalaxy.api.question.ChoiceData
import scommons.react._
import scommons.react.navigation.Theme
import scommons.reactnative._
import scommons.reactnative.ui.{ChoiceGroup, ChoiceGroupProps}

import scala.scalajs.js

case class QuestionChoicesProps(answered: Boolean,
                                choices: List[ChoiceData],
                                selectedIds: Set[Int],
                                setSelectedIds: js.Function1[Set[Int], Unit],
                                multiSelect: Boolean)

object QuestionChoices extends FunctionComponent[QuestionChoicesProps] {

  private[question] var choiceGroupComp: UiComponent[ChoiceGroupProps[Int, ChoiceData]] =
    new ChoiceGroup[Int, ChoiceData]
  private[question] var questionTextComp: UiComponent[QuestionTextProps] = QuestionText
  private[question] var questionAnswerIcon: UiComponent[QuestionAnswerIconProps] = QuestionAnswerIcon

  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    val answered = props.answered

    val iconRenderer: (Boolean, Theme) => ReactElement =
      if (!answered) ChoiceGroupProps.defaultIconRenderer(props.multiSelect)
      else { (_, _) => null }
      
    def renderLabel(data: ChoiceData, theme: Theme): ReactElement = {
      val selected = props.selectedIds.contains(data.id)
      
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
    }
    
    <(choiceGroupComp())(^.wrapped := new ChoiceGroupProps[Int, ChoiceData](
      items = props.choices,
      keyExtractor = _.id,
      iconRenderer = iconRenderer,
      labelRenderer = renderLabel,
      selectedIds = props.selectedIds,
      onSelectChange = { ids =>
        if (!answered) {
          props.setSelectedIds(ids)
        }
      },
      multiSelect = props.multiSelect,
      style = Some(styles.choiceGroup)
    ))()
  }

  private[question] lazy val styles = StyleSheet.create(new Styles)
  private[question] class Styles extends js.Object {
    import ViewStyle._

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
