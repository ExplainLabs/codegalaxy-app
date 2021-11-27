package io.codegalaxy.app.question

import io.codegalaxy.api.question.ChoiceData
import io.codegalaxy.app.question.QuestionChoices._
import scommons.react.navigation._
import scommons.react.test._
import scommons.reactnative.ui.ChoiceGroupProps

class QuestionChoicesSpec extends TestSpec with TestRendererUtils {

  QuestionChoices.choiceGroupComp = mockUiComponent("ChoiceGroup")
  QuestionChoices.questionTextComp = mockUiComponent("QuestionText")
  QuestionChoices.questionAnswerIcon = mockUiComponent("QuestionAnswerIcon")

  private implicit val theme: Theme = DefaultTheme

  it should "call setSelectedIds if not answered" in {
    //given
    val setSelectedIds = mockFunction[Set[Int], Unit]
    val props = QuestionChoicesProps(
      answered = false,
      choices = List(
        ChoiceData(id = 1, choiceText = "Yes"),
        ChoiceData(id = 2, choiceText = "No")
      ),
      selectedIds = Set(1),
      setSelectedIds = setSelectedIds,
      multiSelect = true
    )
    val comp = testRender(<(QuestionChoices())(^.wrapped := props)())
    val choiceProps = findComponentProps(comp, choiceGroupComp)
    val ids = Set(1, 2)
    
    //then
    setSelectedIds.expects(ids)
    
    //when
    choiceProps.onSelectChange(ids)
  }

  it should "not call setSelectedIds if answered" in {
    //given
    val setSelectedIds = mockFunction[Set[Int], Unit]
    val props = QuestionChoicesProps(
      answered = true,
      choices = List(
        ChoiceData(id = 1, choiceText = "Yes"),
        ChoiceData(id = 2, choiceText = "No")
      ),
      selectedIds = Set(1),
      setSelectedIds = setSelectedIds,
      multiSelect = true
    )
    val comp = testRender(<(QuestionChoices())(^.wrapped := props)())
    val choiceProps = findComponentProps(comp, choiceGroupComp)
    val ids = Set(1, 2)
    
    //then
    setSelectedIds.expects(ids).never()
    
    //when
    choiceProps.onSelectChange(ids)
  }

  it should "render not answered / multi select component" in {
    //given
    val props = QuestionChoicesProps(
      answered = false,
      choices = List(
        ChoiceData(id = 1, choiceText = "Yes"),
        ChoiceData(id = 2, choiceText = "No")
      ),
      selectedIds = Set(1),
      setSelectedIds = _ => (),
      multiSelect = true
    )
    
    //when
    val result = testRender(<(QuestionChoices())(^.wrapped := props)())
    
    //then
    assertTestComponent(result, choiceGroupComp) {
      case ChoiceGroupProps(items, keyExtractor, iconRenderer, labelRenderer, selectedIds, _, multiSelect, style) =>
        items shouldBe props.choices
        keyExtractor(props.choices.head) shouldBe 1
        iconRenderer(false, theme) should not be null

        val data = items.head
        val labelComp = createTestRenderer(labelRenderer(data, theme)).root
        assertTestComponent(labelComp, questionTextComp) {
          case QuestionTextProps(textHtml, labelStyle) =>
            textHtml shouldBe data.choiceText
            labelStyle.get.toList shouldBe List(styles.choiceLabel)
        }

        selectedIds shouldBe props.selectedIds
        multiSelect shouldBe props.multiSelect
        style shouldBe Some(styles.choiceGroup)
    }
  }

  it should "render answered / single select component" in {
    //given
    val props = QuestionChoicesProps(
      answered = true,
      choices = List(
        ChoiceData(id = 1, choiceText = "Yes"),
        ChoiceData(id = 2, choiceText = "No")
      ),
      selectedIds = Set(2),
      setSelectedIds = _ => (),
      multiSelect = false
    )
    
    //when
    val result = testRender(<(QuestionChoices())(^.wrapped := props)())
    
    //then
    assertTestComponent(result, choiceGroupComp) {
      case ChoiceGroupProps(items, keyExtractor, iconRenderer, labelRenderer, selectedIds, _, multiSelect, style) =>
        items shouldBe props.choices
        keyExtractor(props.choices.head) shouldBe 1
        iconRenderer(false, theme) shouldBe null

        val data = items.head
        val labelComp = createTestRenderer(labelRenderer(data, theme)).root
        val (iconComp, choiceLabel) = inside(labelComp.children.toList) {
          case List(icon, label) => (icon, label)
        }
        assertTestComponent(iconComp, questionAnswerIcon) { case QuestionAnswerIconProps(correct) =>
          correct shouldBe data.correct.getOrElse(false)
        }
        assertTestComponent(choiceLabel, questionTextComp) {
          case QuestionTextProps(textHtml, labelStyle) =>
            textHtml shouldBe data.choiceText
            labelStyle.get.toList shouldBe List(styles.choiceLabel, styles.choiceNotSelected)
        }

        selectedIds shouldBe props.selectedIds
        multiSelect shouldBe props.multiSelect
        style shouldBe Some(styles.choiceGroup)
    }
  }

  it should "render answered and selected choice" in {
    //given
    val props = QuestionChoicesProps(
      answered = true,
      choices = List(
        ChoiceData(id = 1, choiceText = "Yes"),
        ChoiceData(id = 2, choiceText = "No")
      ),
      selectedIds = Set(2),
      setSelectedIds = _ => (),
      multiSelect = false
    )
    
    //when
    val result = testRender(<(QuestionChoices())(^.wrapped := props)())
    
    //then
    assertTestComponent(result, choiceGroupComp) {
      case ChoiceGroupProps(items, keyExtractor, iconRenderer, labelRenderer, selectedIds, _, multiSelect, style) =>
        items shouldBe props.choices
        keyExtractor(props.choices.head) shouldBe 1
        iconRenderer(false, theme) shouldBe null

        val data = items(1)
        val labelComp = createTestRenderer(labelRenderer(data, theme)).root
        val (iconComp, choiceLabel) = inside(labelComp.children.toList) {
          case List(icon, label) => (icon, label)
        }
        assertTestComponent(iconComp, questionAnswerIcon) { case QuestionAnswerIconProps(correct) =>
          correct shouldBe data.correct.getOrElse(false)
        }
        assertTestComponent(choiceLabel, questionTextComp) {
          case QuestionTextProps(textHtml, labelStyle) =>
            textHtml shouldBe data.choiceText
            labelStyle.get.toList shouldBe List(styles.choiceLabel)
        }

        selectedIds shouldBe props.selectedIds
        multiSelect shouldBe props.multiSelect
        style shouldBe Some(styles.choiceGroup)
    }
  }
}
