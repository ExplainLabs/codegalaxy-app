package io.codegalaxy.app.chapter

import io.codegalaxy.api.chapter._
import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.ChapterEntity
import io.codegalaxy.domain.dao.ChapterDao

import scala.concurrent.Future

class ChapterServiceSpec extends BaseDBContextSpec {

  it should "fetch chapters and save them in DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c1 = getChapterRespData("chapter1")
    val c2 = getChapterRespData("chapter2")

    (api.getChapters _).expects(topic).returning(Future.successful(List(c1, c2)))

    val beforeF = dao.deleteAll()
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.fetch(topic)
    }

    //then
    for {
      resList <- resultF
      chaptersList <- dao.list(topic)
    } yield {
      resList shouldBe chaptersList
      resList shouldBe Seq(
        toChapterEntity(topic, c1).copy(id = 1),
        toChapterEntity(topic, c2).copy(id = 2)
      )
    }
  }
  
  it should "refresh chapters in DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c1 = getChapterRespData("chapter1")
    val c2 = getChapterRespData("chapter2")
    val newChapter = getChapterRespData("newChapter")

    (api.getChapters _).expects(topic).returning(Future.successful(List(c1, newChapter)))

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(topic, Seq(
        toChapterEntity(topic, c1),
        toChapterEntity(topic, c2)
      ))
      Some(existing) <- service.getByAlias(topic, c1.chapter.alias)
    } yield existing

    //when
    val resultF = for {
      existing <- beforeF
      res <- service.fetch(topic, refresh = true)
    } yield (existing, res)

    //then
    for {
      (existing, resList) <- resultF
      chaptersList <- dao.list(topic)
    } yield {
      resList shouldBe chaptersList
      resList shouldBe Seq(
        existing,
        toChapterEntity(topic, newChapter).copy(id = 3)
      )
    }
  }
  
  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c1 = getChapterRespData("chapter1")
    val c2 = getChapterRespData("chapter2")

    (api.getChapters _).expects(*).never()

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(topic, Seq(
        toChapterEntity(topic, c1),
        toChapterEntity(topic, c2)
      ))
    } yield ()

    //when
    val resultF = for {
      _ <- beforeF
      res <- service.fetch(topic)
    } yield res

    //then
    for {
      resList <- resultF
      chaptersList <- dao.list(topic)
    } yield {
      resList shouldBe chaptersList
      resList shouldBe Seq(
        toChapterEntity(topic, c1).copy(id = 1),
        toChapterEntity(topic, c2).copy(id = 2)
      )
    }
  }
  
  private def getChapterRespData(alias: String): ChapterRespData = {
    ChapterRespData(
      chapter = ChapterData(
        alias = alias,
        name = "Test Chapter",
        info = InfoData(
          numberOfQuestions = 1,
          numberOfPaid = 2,
          numberOfLearners = 3,
          numberOfChapters = 4
        )
      ),
      stats = StatsData(
        progressAll = 99
      )
    )
  }

  private def toChapterEntity(topic: String, resp: ChapterRespData): ChapterEntity = {
    val data = resp.chapter
    ChapterEntity(
      id = -1,
      topic = topic,
      alias = data.alias,
      name = data.name,
      numQuestions = data.info.numberOfQuestions,
      numPaid = data.info.numberOfPaid,
      numLearners = data.info.numberOfLearners,
      numChapters = data.info.numberOfChapters,
      progress = resp.stats.progressAll
    )
  }
}
