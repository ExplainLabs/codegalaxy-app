package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionView._
import scommons.react.test._

class QuestionViewSpec extends TestSpec with TestRendererUtils {

  QuestionView.questionTextComp = mockUiComponent("QuestionText")
  QuestionView.questionChoicesComp = mockUiComponent("QuestionChoices")
  QuestionView.questionAnswerComp = mockUiComponent("QuestionAnswer")
  QuestionView.questionRuleComp = mockUiComponent("QuestionRule")
  QuestionView.questionButtonComp = mockUiComponent("QuestionButton")

  it should "update selectedIds when setSelectedIds" in {
    //given
    val props = getQuestionViewProps()
    val renderer = createTestRenderer(<(QuestionView())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, questionChoicesComp)
    choiceComp.selectedIds shouldBe Set.empty
    val ids = Set(1, 2)

    //when
    choiceComp.setSelectedIds(ids)
    
    //then
    inside(findComponentProps(renderer.root, questionChoicesComp)) { case choice =>
      choice.selectedIds shouldBe ids
    }
  }

  it should "call onSubmitAnswer when onPress Continue button" in {
    //given
    val onSubmitAnswer = mockFunction[QuestionData, Unit]
    val props = getQuestionViewProps(onSubmitAnswer = onSubmitAnswer)
    val renderer = createTestRenderer(<(QuestionView())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, questionChoicesComp)
    val selectedChoiceId = 1
    choiceComp.setSelectedIds(Set(selectedChoiceId))
    inside(findComponentProps(renderer.root, questionChoicesComp)) { case choice =>
      choice.selectedIds shouldBe Set(selectedChoiceId)
    }
    val buttonProps = findComponentProps(renderer.root, questionButtonComp)
    buttonProps.text shouldBe "Continue"
    val data = {
      props.data.copy(choices = props.data.choices.map { choice =>
        val selected = choice.id == selectedChoiceId
        choice.copy(selected = if (selected) Some(true) else Some(false))
      })
    }

    //then
    onSubmitAnswer.expects(data)

    //when
    buttonProps.onPress()
  }

  it should "call onNextQuestion when onPress Next button" in {
    //given
    val onNextQuestion = mockFunction[Unit]
    val props = getQuestionViewProps(onNextQuestion = onNextQuestion)
    val renderer = createTestRenderer(<(QuestionView())(^.wrapped := props)())
    val choiceComp = findComponentProps(renderer.root, questionChoicesComp)
    choiceComp.setSelectedIds(Set(1))
    inside(findComponentProps(renderer.root, questionChoicesComp)) { case choice =>
      choice.selectedIds shouldBe Set(1)
    }
    val updatedProps = {
      props.copy(data = props.data.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))
    }
    renderer.update(<(QuestionView())(^.wrapped := updatedProps)())
    val buttonProps = findComponentProps(renderer.root, questionButtonComp)
    buttonProps.text shouldBe "Next"

    //then
    onNextQuestion.expects()

    //when
    buttonProps.onPress()

    //then
    inside(findComponentProps(renderer.root, questionChoicesComp)) { case choice =>
      choice.selectedIds shouldBe Set.empty
    }
  }

  it should "render un-answered question data" in {
    //given
    val props = getQuestionViewProps()
    val question = props.data
    props.data.correct shouldBe None
    
    //when
    val result = createTestRenderer(<(QuestionView())(^.wrapped := props)()).root

    //then
    inside(result.children.toList) { case List(questionText, choice, button) =>
      assertTestComponent(questionText, questionTextComp) { case QuestionTextProps(html, style) =>
        html shouldBe question.text
        style shouldBe None
      }
      assertTestComponent(choice, questionChoicesComp) {
        case QuestionChoicesProps(resAnswered, choices, selectedIds, _, multiSelect) =>
          resAnswered shouldBe false
          choices shouldBe question.choices
          selectedIds shouldBe Set.empty
          multiSelect shouldBe false
      }
      assertTestComponent(button, questionButtonComp) { case QuestionButtonProps(text, _) =>
        text shouldBe "Continue"
      }
    }
  }

  it should "render answered incorrect question data" in {
    //given
    val props = {
      val props = getQuestionViewProps()
      props.copy(data = props.data.copy(
        correct = Some(false),
        rules = List(RuleData("test rule title", "test rule text")),
        explanation = Some("test explanation")
      ))
    }
    val question = props.data

    //when
    val result = createTestRenderer(<(QuestionView())(^.wrapped := props)()).root

    //then
    inside(result.children.toList) { case List(questionText, choice, answer, ruleComp, explanation, button) =>
      assertTestComponent(questionText, questionTextComp) { case QuestionTextProps(html, style) =>
        html shouldBe question.text
        style shouldBe None
      }
      assertTestComponent(choice, questionChoicesComp) {
        case QuestionChoicesProps(resAnswered, choices, selectedIds, _, multiSelect) =>
          resAnswered shouldBe true
          choices shouldBe question.choices
          selectedIds shouldBe Set.empty
          multiSelect shouldBe false
      }
      assertTestComponent(answer, questionAnswerComp) { case QuestionAnswerProps(correct) =>
        correct shouldBe false
      }
      assertTestComponent(ruleComp, questionRuleComp) {
        case QuestionRuleProps(resTitle, resText) =>
          val rule = question.rules.head
          resTitle shouldBe rule.title
          resText shouldBe rule.text
      }
      assertTestComponent(explanation, questionRuleComp) {
        case QuestionRuleProps(resTitle, resText) =>
          resTitle shouldBe "Explanation"
          Some(resText) shouldBe question.explanation
      }
      assertTestComponent(button, questionButtonComp) { case QuestionButtonProps(text, _) =>
        text shouldBe "Next"
      }
    }
  }

  it should "render answered correct question data" in {
    //given
    val props = {
      val props = getQuestionViewProps()
      props.copy(data = props.data.copy(
        choices = props.data.choices.map(_.copy(correct = Some(true))),
        correct = Some(true),
        rules = Nil,
        explanation = None,
        answerType = "MULTIPLE_CHOICES"
      ))
    }
    val question = props.data

    //when
    val result = createTestRenderer(<(QuestionView())(^.wrapped := props)()).root

    //then
    inside(result.children.toList) { case List(questionText, choice, answer, button) =>
      assertTestComponent(questionText, questionTextComp) { case QuestionTextProps(html, style) =>
        html shouldBe question.text
        style shouldBe None
      }
      assertTestComponent(choice, questionChoicesComp) {
        case QuestionChoicesProps(resAnswered, choices, selectedIds, _, multiSelect) =>
          resAnswered shouldBe true
          choices shouldBe question.choices
          selectedIds shouldBe Set.empty
          multiSelect shouldBe true
      }
      assertTestComponent(answer, questionAnswerComp) { case QuestionAnswerProps(correct) =>
        correct shouldBe true
      }
      assertTestComponent(button, questionButtonComp) { case QuestionButtonProps(text, _) =>
        text shouldBe "Next"
      }
    }
  }

  private def getQuestionViewProps(data: QuestionData = QuestionData(
                                     uuid = "14h15kl1h514l5h4315j145lj1",
                                     text = "Can methods, taking one argument, be used with infix syntax?",
                                     answerType = "SINGLE_CHOICE",
                                     choices = List(
                                       ChoiceData(
                                         id = 1,
                                         choiceText = "Yes"
                                       ),
                                       ChoiceData(
                                         id = 2,
                                         choiceText = "No"
                                       )
                                     )
                                   ),
                                   onSubmitAnswer: QuestionData => Unit = mockFunction[QuestionData, Unit],
                                   onNextQuestion: () => Unit = mockFunction[Unit]
                                  ): QuestionViewProps = {
    QuestionViewProps(
      data = data,
      onSubmitAnswer = onSubmitAnswer,
      onNextQuestion = onNextQuestion
    )
  }
}
