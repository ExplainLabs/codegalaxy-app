package io.codegalaxy.app.chapter

import io.codegalaxy.app.chapter.ChapterActions._
import io.codegalaxy.app.chapter.ChapterActionsSpec._
import io.codegalaxy.domain.{Chapter, ChapterEntity, ChapterStats}
import scommons.nodejs.test.AsyncTestSpec
import scommons.react.redux.task.FutureTask

import scala.concurrent.Future

class ChapterActionsSpec extends AsyncTestSpec {

  //noinspection TypeAnnotation
  class ChapterService {
    val fetch = mockFunction[String, Boolean, Future[Seq[Chapter]]]

    val service = new MockChapterService(fetchMock = fetch)
  }

  it should "dispatch ChaptersFetchedAction when fetchChapters" in {
    //given
    val chapterService = mock[ChapterService]
    val actions = new ChapterActionsTest(chapterService.service)
    val dispatch = mockFunction[Any, Any]
    val topic = "test_topic"
    val chapter = Chapter(
      entity = ChapterEntity(
        id = 1,
        topic = topic,
        alias = "test_chapter",
        name = "Test Chapter",
        numQuestions = 1,
        numPaid = 2,
        numLearners = 3,
        numChapters = 4,
        numTheory = Some(5)
      ),
      stats = Some(ChapterStats(
        id = 1,
        progress = 10,
        progressOnce = 20,
        progressAll = 100,
        freePercent = 30,
        paid = 40
      ))
    )
    val dataList = List(chapter)
    val refresh = true

    //then
    chapterService.fetch.expects(*, *).onCall { (t, r) =>
      t shouldBe topic
      r shouldBe refresh
      Future.successful(dataList)
    }
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
