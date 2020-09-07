package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.app.chapter.ChapterActionsSpec._
import io.codegalaxy.domain.ChapterEntity
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class ChapterActionsSpec extends AsyncTestSpec {

  it should "dispatch ChaptersFetchedAction when fetchChapters" in {
    //given
    val chapterService = mock[ChapterService]
    val actions = new ChapterActionsTest(chapterService)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = ChapterEntity(
      id = 1,
      topic = topic,
      alias = "test_chapter",
      name = "Test Chapter",
      numQuestions = 1,
      numPaid = 2,
      numLearners = 3,
      numChapters = 4,
      progress = 5
    )
    val dataList = List(chapter)
    val refresh = true

    //then
    (chapterService.fetch _).expects(topic, refresh).returning(Future.successful(dataList))
    dispatch.expects(ChaptersFetchedAction(topic, dataList))

    //when
    val ChaptersFetchAction(resTopic, FutureTask(message, future)) =
      actions.fetchChapters(dispatch, topic, refresh)

    //then
    resTopic shouldBe topic
    message shouldBe s"Fetching $topic chapters"
    future.map { resp =>
      resp shouldBe dataList
    }
  }
}

object ChapterActionsSpec {

  private class ChapterActionsTest(service: ChapterService)
    extends ChapterActions {

    protected def chapterService: ChapterService = service
  }
}
