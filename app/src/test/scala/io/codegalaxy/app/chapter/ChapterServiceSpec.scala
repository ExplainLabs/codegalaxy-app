package io.codegalaxy.app.chapter

import io.codegalaxy.api.chapter._
import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.dao.ChapterDao
import io.codegalaxy.domain.{Chapter, ChapterEntity, ChapterStats}

import scala.concurrent.Future

class ChapterServiceSpec extends BaseDBContextSpec {

  it should "fetch chapters with statistics and save them in DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c1 = getChapterRespData("chapter1", multiplier = 1)
    val c2 = {
      val data = getChapterRespData("chapter2", multiplier = 2)
      data.copy(chapter = data.chapter.copy(info = None))
    }

    (api.getChaptersWithStatistics _).expects(topic).returning(Future.successful(List(c1, c2)))

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
        toChapter(topic, c1, id = 1),
        toChapter(topic, c2, id = 2)
      )
    }
  }
  
  it should "refresh chapters with statistics in DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c0 = getChapterRespData("chapter0", multiplier = 10)
    val c1 = getChapterRespData("chapter1", multiplier = 1)
    val c2 = getChapterRespData("chapter2", multiplier = 2)
    val newChapter = getChapterRespData("newChapter", multiplier = 3)

    (api.getChaptersWithStatistics _).expects(topic).returning(Future.successful(List(c1, newChapter)))

    val otherTopic = "other_topic"
    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.saveAll(otherTopic, Seq(toChapter(otherTopic, c0)))
      _ <- dao.saveAll(topic, Seq(
        toChapter(topic, c1),
        toChapter(topic, c2)
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
      other <- service.getByAlias(otherTopic, c0.chapter.alias)
    } yield {
      other shouldBe Some(toChapter(otherTopic, c0, id = 1))
      resList shouldBe chaptersList
      resList shouldBe Seq(
        existing,
        toChapter(topic, newChapter, id = 3)
      )
    }
  }
  
  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = mock[ChapterApi]
    val dao = new ChapterDao(ctx)
    val service = new ChapterService(api, dao)
    val topic = "test_topic"
    val c1 = getChapterRespData("chapter1", multiplier = 1)
    val c2 = getChapterRespData("chapter2", multiplier = 2)

    (api.getChaptersWithStatistics _).expects(*).never()

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.saveAll(topic, Seq(
        toChapter(topic, c1),
        toChapter(topic, c2)
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
        toChapter(topic, c1, id = 1),
        toChapter(topic, c2, id = 2)
      )
    }
  }
  
  private def getChapterRespData(alias: String, multiplier: Int): ChapterWithStatisticsRespData = {
    ChapterWithStatisticsRespData(
      chapter = ChapterData(
        alias = alias,
        name = "Test Chapter",
        info = Some(InfoData(
          numberOfQuestions = 1,
          numberOfPaid = 2,
          numberOfLearners = 3,
          numberOfChapters = 4,
          numberOfTheory = Some(5)
        ))
      ),
      stats = StatsData(
        progress = multiplier * 10,
        progressOnce = multiplier * 20,
        progressAll = multiplier * 30,
        freePercent = multiplier * 40,
        paid = multiplier * 50
      )
    )
  }

  private def toChapter(topic: String, resp: ChapterWithStatisticsRespData, id: Int = -1): Chapter = {
    val data = resp.chapter
    val info = data.info.getOrElse(InfoData())
    
    Chapter(
      entity = ChapterEntity(
        id = id,
        topic = topic,
        alias = data.alias,
        name = data.name,
        numQuestions = info.numberOfQuestions,
        numPaid = info.numberOfPaid,
        numLearners = info.numberOfLearners,
        numChapters = info.numberOfChapters,
        numTheory = info.numberOfTheory
      ),
      stats = Some(ChapterStats(
        id = id,
        progress = resp.stats.progress,
        progressOnce = resp.stats.progressOnce,
        progressAll = resp.stats.progressAll,
        freePercent = resp.stats.freePercent,
        paid = resp.stats.paid
      ))
    )
  }
}
