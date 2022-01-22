package io.codegalaxy.app

import io.codegalaxy.app.chapter.ChapterState
import io.codegalaxy.app.question.QuestionState
import io.codegalaxy.app.topic.TopicState
import io.codegalaxy.app.user.UserState
import scommons.react.redux.task.AbstractTask

//noinspection NotImplementedCode
class MockCodeGalaxyState(
  currentTaskMock: () => Option[AbstractTask] = () => ???,
  userStateMock: () => UserState = () => ???,
  topicStateMock: () => TopicState = () => ???,
  chapterStateMock: () => ChapterState = () => ???,
  questionStateMock: () => QuestionState = () => ???
) extends CodeGalaxyStateDef {

  override def currentTask: Option[AbstractTask] = currentTaskMock()

  override def userState: UserState = userStateMock()

  override def topicState: TopicState = topicStateMock()

  override def chapterState: ChapterState = chapterStateMock()

  override def questionState: QuestionState = questionStateMock()
}
