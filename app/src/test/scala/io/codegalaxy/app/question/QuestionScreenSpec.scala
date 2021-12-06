package io.codegalaxy.app.question

import io.codegalaxy.api.question._
import io.codegalaxy.app.question.QuestionActions._
import io.codegalaxy.app.question.QuestionScreen._
import io.codegalaxy.app.topic.TopicParams
import org.scalatest.Succeeded
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.navigation._
import scommons.react.redux.Dispatch
import scommons.react.redux.task.FutureTask
import scommons.react.test._
import scommons.reactnative.ScrollView._
import scommons.reactnative._
import scommons.reactnative.safearea.SafeArea._
import scommons.reactnative.safearea._

import scala.concurrent.Future

class QuestionScreenSpec extends AsyncTestSpec with BaseTestSpec with TestRendererUtils {

  QuestionScreen.questionViewComp = mockUiComponent("QuestionView")

  it should "dispatch actions when onSubmitAnswer" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val viewComp = findComponentProps(renderer.root, questionViewComp)
    val resp = mock[QuestionData]
    val topic = inside(props.data.topic) {
      case Some(topic) => topic
    }
    val chapter = inside(props.data.chapter) {
      case Some(chapter) => chapter
    }
    val submitAction = AnswerSubmitAction(FutureTask("Submitting...", Future.successful(resp)))
    val data = inside(props.data.question) {
      case Some(question) => question.copy(text = "Answering...")
    }

    //then
    (actions.submitAnswer _).expects(dispatch, topic, chapter, data).returning(submitAction)
    dispatch.expects(submitAction)

    //when
    viewComp.onSubmitAnswer(data)

    //then
    submitAction.task.future.map(_ => Succeeded)
  }

  it should "dispatch actions when onNextQuestion" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    val renderer = createTestRenderer(<(QuestionScreen())(^.wrapped := props)())
    val updatedProps = {
      val question = inside(props.data.question) {
        case Some(question) => question
      }
      props.copy(data = props.data.copy(question = Some(question.copy(
        correct = Some(false),
        rules = Nil,
        explanation = None
      ))))
    }
    renderer.update(<(QuestionScreen())(^.wrapped := updatedProps)())
    val viewComp = findComponentProps(renderer.root, questionViewComp)
    val topic = inside(updatedProps.data.topic) {
      case Some(topic) => topic
    }
    val chapter = inside(updatedProps.data.chapter) {
      case Some(chapter) => chapter
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching...",
      Future.successful(data)))

    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    viewComp.onNextQuestion()

    //then
    fetchAction.task.future.map(_ => Succeeded)
  }

  it should "dispatch actions if topic is changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val topic = "new_topic"
    val (props, chapter) = {
      val props = getQuestionScreenProps(dispatch, actions)
      val chapter = inside(props.data.chapter) {
        case Some(chapter) => chapter
      }
      (props.copy(params = props.params.copy(topic = topic)), chapter)
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching Question",
      Future.successful(data)))
    
    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    fetchAction.task.future.map(_ => Succeeded)
  }

  it should "dispatch actions if chapter is changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val chapter = "new_chapter"
    val (props, topic) = {
      val props = getQuestionScreenProps(dispatch, actions)
      val topic = inside(props.data.topic) {
        case Some(topic) => topic
      }
      (props.copy(params = props.params.copy(chapter = Some(chapter))), topic)
    }
    val data = mock[QuestionData]
    val fetchAction = QuestionFetchAction(topic, chapter, FutureTask("Fetching Question",
      Future.successful(data)))
    
    //then
    (actions.fetchQuestion _).expects(dispatch, topic, chapter).returning(fetchAction)
    dispatch.expects(fetchAction)

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    fetchAction.task.future.map(_ => Succeeded)
  }

  it should "do not dispatch actions if params not changed when mount" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val actions = mock[QuestionActions]
    val props = getQuestionScreenProps(dispatch, actions)
    
    //then
    (actions.fetchQuestion _).expects(*, *, *).never()

    //when
    testRender(<(QuestionScreen())(^.wrapped := props)())

    Succeeded
  }

  it should "render Loading... if no question data" in {
    //given
    val props =  {
      val props = getQuestionScreenProps()
      props.copy(data = props.data.copy(question = None))
    }
    
    //when
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    implicit val theme: Theme = DefaultTheme
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(
        <.Text(^.rnStyle := themeTextStyle)("Loading...")
      )
    )
  }

  it should "render question view" in {
    //given
    val props = getQuestionScreenProps()
    
    //when
    val result = testRender(<(QuestionScreen())(^.wrapped := props)())

    //then
    assertNativeComponent(result,
      <.SafeAreaView(
        ^.rnStyle := styles.container,
        ^.edges := List(SafeAreaEdge.left, SafeAreaEdge.bottom, SafeAreaEdge.right)
      )(
        <.ScrollView(^.keyboardShouldPersistTaps := KeyboardShouldPersistTaps.always)(
          <(questionViewComp())()()
        )
      )
    )
    inside(findComponentProps(result, questionViewComp)) {
      case QuestionViewProps(question, _, _) =>
        Some(question) shouldBe props.data.question
    }
  }

  private def getQuestionScreenProps(dispatch: Dispatch = mock[Dispatch],
                                     actions: QuestionActions = mock[QuestionActions],
                                     data: QuestionState = QuestionState(
                                       topic = Some("test_topic"),
                                       chapter = Some("test_chapter"),
                                       question = Some(QuestionData(
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
                                       ))
                                     ),
                                     params: TopicParams = TopicParams("test_topic", Some("test_chapter"))
                                    ): QuestionScreenProps = {
    QuestionScreenProps(
      dispatch = dispatch,
      actions = actions,
      data = data,
      params = params
    )
  }
}
