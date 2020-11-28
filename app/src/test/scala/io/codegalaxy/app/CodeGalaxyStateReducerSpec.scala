package io.codegalaxy.app

import io.codegalaxy.app.chapter.ChapterState
import io.codegalaxy.app.question.QuestionState
import io.codegalaxy.app.topic.TopicState
import io.codegalaxy.app.user.UserActions.UserLoginAction
import io.codegalaxy.app.user.UserState
import io.codegalaxy.domain.{ConfigEntity, ProfileEntity}
import scommons.react.redux.task.FutureTask
import scommons.react.test.TestSpec

import scala.concurrent.Future

class CodeGalaxyStateReducerSpec extends TestSpec {

  it should "return initial state" in {
    //when
    val result = CodeGalaxyStateReducer.reduce(None, "")

    //then
    inside(result) {
      case CodeGalaxyState(
        currentTask,
        userState,
        topicState,
        chapterState,
        questionState
      ) =>
        currentTask shouldBe None
        userState shouldBe UserState()
        topicState shouldBe TopicState()
        chapterState shouldBe ChapterState()
        questionState shouldBe QuestionState()
    }
  }

  it should "set currentTask when TaskAction" in {
    //given
    val initialState = CodeGalaxyStateReducer.reduce(None, "")
    val task = FutureTask("test task", Future.successful((Option.empty[ProfileEntity], Option.empty[ConfigEntity])))
    initialState.currentTask shouldBe None

    //when
    val result = CodeGalaxyStateReducer.reduce(Some(initialState), UserLoginAction(task))

    //then
    result.currentTask shouldBe Some(task)
  }
}
