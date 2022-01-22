package io.codegalaxy.app.stats

import io.codegalaxy.api.stats._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain._
import io.codegalaxy.domain.dao.{ChapterDao, TopicDao}

import scala.concurrent.Future

class StatsServiceSpec extends BaseDBContextSpec {

  //noinspection TypeAnnotation
  class Api {
    val getStats = mockFunction[Future[List[StatsRespData]]]
    val getStatsTopic = mockFunction[String, Future[StatsData]]
    val getStatsChapter = mockFunction[String, String, Future[StatsData]]

    val api = new MockStatsApi(
      getStatsMock = getStats,
      getStatsTopicMock = getStatsTopic,
      getStatsChapterMock = getStatsChapter
    )
  }

  it should "fail if topic doesn't exist" in withCtx { ctx =>
    //given
    val api = new Api
    val topicDao = new TopicDao(ctx)
    val chapterDao = new ChapterDao(ctx)
    val service = new StatsService(api.api, topicDao, chapterDao)
    val topicStatsData = getStatsData(multiplier = 1)
    val chapterStatsData = getStatsData(multiplier = 2)
    val topic = "test_topic"
    val chapter = "test_chapter"

    api.getStatsTopic.expects(*).onCall { t: String =>
      t shouldBe topic
      Future.successful(topicStatsData)
    }
    api.getStatsChapter.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(chapterStatsData)
    }

    val beforeF = for {
      _ <- topicDao.deleteAll()
      _ <- chapterDao.deleteAll()
      _ <- chapterDao.saveAll(topic, List(toChapter(topic, chapter)))
    } yield ()

    //when
    val resultF = beforeF.flatMap { _ =>
      service.updateStats(topic, chapter)
    }

    //then
    resultF.failed.map { ex =>
      ex.getMessage shouldBe s"TopicEntity not found: $topic"
    }
  }

  it should "fail if chapter doesn't exist" in withCtx { ctx =>
    //given
    val api = new Api
    val topicDao = new TopicDao(ctx)
    val chapterDao = new ChapterDao(ctx)
    val service = new StatsService(api.api, topicDao, chapterDao)
    val topicStatsData = getStatsData(multiplier = 1)
    val chapterStatsData = getStatsData(multiplier = 2)
    val topic = "test_topic"
    val chapter = "test_chapter"

    api.getStatsTopic.expects(*).onCall { t: String =>
      t shouldBe topic
      Future.successful(topicStatsData)
    }
    api.getStatsChapter.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(chapterStatsData)
    }

    val beforeF = for {
      _ <- topicDao.deleteAll()
      _ <- chapterDao.deleteAll()
      _ <- topicDao.saveAll(List(toTopic(topic)))
    } yield ()

    //when
    val resultF = beforeF.flatMap { _ =>
      service.updateStats(topic, chapter)
    }

    //then
    resultF.failed.map { ex =>
      ex.getMessage shouldBe s"ChapterEntity not found: $topic/$chapter"
    }
  }

  it should "fetch stats and insert them into DB" in withCtx { ctx =>
    //given
    val api = new Api
    val topicDao = new TopicDao(ctx)
    val chapterDao = new ChapterDao(ctx)
    val service = new StatsService(api.api, topicDao, chapterDao)
    val topicStatsData = getStatsData(multiplier = 1)
    val chapterStatsData = getStatsData(multiplier = 2)
    val topic = "test_topic"
    val chapter = "test_chapter"

    api.getStatsTopic.expects(*).onCall { t: String =>
      t shouldBe topic
      Future.successful(topicStatsData)
    }
    api.getStatsChapter.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(chapterStatsData)
    }

    val beforeF = for {
      _ <- topicDao.deleteAll()
      _ <- chapterDao.deleteAll()
      _ <- topicDao.saveAll(List(toTopic(topic)))
      _ <- chapterDao.saveAll(topic, List(toChapter(topic, chapter)))
    } yield ()
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.updateStats(topic, chapter)
    }

    //then
    for {
      (resTopicStats, resChapterStats) <- resultF
      Some(Topic(_, Some(topicStats))) <- topicDao.getByAlias(topic)
      Some(Chapter(_, Some(chapterStats))) <- chapterDao.getByAlias(topic, chapter)
    } yield {
      resTopicStats shouldBe topicStats
      resChapterStats shouldBe chapterStats
      resTopicStats shouldBe toTopicStats(1, topicStatsData)
      resChapterStats shouldBe toChapterStats(1, chapterStatsData)
    }
  }

  it should "fetch stats and update existing stats in DB" in withCtx { ctx =>
    //given
    val api = new Api
    val topicDao = new TopicDao(ctx)
    val chapterDao = new ChapterDao(ctx)
    val service = new StatsService(api.api, topicDao, chapterDao)
    val topicStatsData = getStatsData(multiplier = 1)
    val chapterStatsData = getStatsData(multiplier = 2)
    val topic = "test_topic"
    val chapter = "test_chapter"

    api.getStatsTopic.expects(*).onCall { t: String =>
      t shouldBe topic
      Future.successful(topicStatsData)
    }
    api.getStatsChapter.expects(*, *).onCall { (t, c) =>
      t shouldBe topic
      c shouldBe chapter
      Future.successful(chapterStatsData)
    }

    val beforeF = for {
      _ <- topicDao.deleteAll()
      _ <- chapterDao.deleteAll()
      _ <- topicDao.saveAll(List(
        toTopic(topic, Some(toTopicStats(1, getStatsData(multiplier = 3))))
      ))
      _ <- chapterDao.saveAll(topic, List(
        toChapter(topic, chapter, Some(toChapterStats(1, getStatsData(multiplier = 4))))
      ))
    } yield ()

    //when
    val resultF = beforeF.flatMap { _ =>
      service.updateStats(topic, chapter)
    }

    //then
    for {
      (resTopicStats, resChapterStats) <- resultF
      Some(Topic(_, Some(topicStats))) <- topicDao.getByAlias(topic)
      Some(Chapter(_, Some(chapterStats))) <- chapterDao.getByAlias(topic, chapter)
    } yield {
      resTopicStats shouldBe topicStats
      resChapterStats shouldBe chapterStats
      resTopicStats shouldBe toTopicStats(1, topicStatsData)
      resChapterStats shouldBe toChapterStats(1, chapterStatsData)
    }
  }
  
  private def getStatsData(multiplier: Int): StatsData = {
    StatsData(
      progress = multiplier * 1,
      progressOnce = multiplier * 2,
      progressAll = multiplier * 3,
      freePercent = multiplier * 4,
      paid = multiplier * 5
    )
  }

  private def toTopic(alias: String, stats: Option[TopicStats] = None): Topic = {
    Topic(
      entity = TopicEntity(
        id = -1,
        alias = alias,
        name = "test_topic_name",
        lang = "en",
        numQuestions = 1,
        numPaid = 2,
        numLearners = 3,
        numChapters = 4,
        numTheory = Some(5),
        svgIcon = None
      ),
      stats = stats
    )
  }
  
  private def toChapter(topic: String, alias: String, stats: Option[ChapterStats] = None): Chapter = {
    Chapter(
      entity = ChapterEntity(
        id = -1,
        topic = topic,
        alias = alias,
        name = "test_chapter_name",
        numQuestions = 11,
        numPaid = 22,
        numLearners = 33,
        numChapters = 44,
        numTheory = Some(55)
      ),
      stats = stats
    )
  }

  private def toTopicStats(id: Int, stats: StatsData): TopicStats = {
    TopicStats(
      id = id,
      progress = stats.progress,
      progressOnce = stats.progressOnce,
      progressAll = stats.progressAll,
      freePercent = stats.freePercent,
      paid = stats.paid
    )
  }
  
  private def toChapterStats(id: Int, stats: StatsData): ChapterStats = {
    ChapterStats(
      id = id,
      progress = stats.progress,
      progressOnce = stats.progressOnce,
      progressAll = stats.progressAll,
      freePercent = stats.freePercent,
      paid = stats.paid
    )
  }
}
