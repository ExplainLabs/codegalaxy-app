package io.codegalaxy.app.topic

import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.dao.TopicDao
import io.codegalaxy.domain.{Topic, TopicEntity, TopicStats}

import scala.concurrent.Future

class TopicServiceSpec extends BaseDBContextSpec {

  //noinspection TypeAnnotation
  class Api {
    val getStats = mockFunction[Future[List[StatsRespData]]]
    val getTopics = mockFunction[Future[List[TopicWithInfoData]]]
    val getTopicIcon = mockFunction[String, Future[Option[String]]]
    
    val api = new MockTopicWithStatsApi(
      getStatsMock = getStats,
      getTopicsMock = getTopics,
      getTopicIconMock = getTopicIcon
    )
  }

  it should "fetch topics and save them in DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new TopicDao(ctx)
    val service = new TopicService(api.api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1", multiplier = 1)

    api.getTopics.expects().returning(Future.successful(List(t1, t2)))
    api.getTopicIcon.expects(*).onCall { a: String =>
      a shouldBe t1.alias
      Future.successful(Some(svg1))
    }
    api.getTopicIcon.expects(*).onCall { a: String =>
      a shouldBe t2.alias
      Future.successful(Some(svg2))
    }
    api.getStats.expects().returning(Future.successful(List(stats)))

    val beforeF = dao.deleteAll()
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.fetch()
    }

    //then
    for {
      resList <- resultF
      topicsList <- dao.list()
    } yield {
      resList shouldBe topicsList
      resList shouldBe Seq(
        toTopic(t1, Some(svg1), Some(stats.statistics), id = 1),
        toTopic(t2, Some(svg2), None, id = 2)
      )
    }
  }
  
  it should "refresh topics in DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new TopicDao(ctx)
    val service = new TopicService(api.api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1", multiplier = 1)
    val newTopic = getTopicData("newTopic")
    val newSvg = "new svg"
    val newStats = getStatsRespData("newTopic", multiplier = 2)

    api.getTopics.expects().returning(Future.successful(List(t1, newTopic)))
    api.getTopicIcon.expects(*).onCall { a: String =>
      a shouldBe t1.alias
      Future.successful(Some(svg1))
    }
    api.getTopicIcon.expects(*).onCall { a: String =>
      a shouldBe newTopic.alias
      Future.successful(Some(newSvg))
    }
    api.getStats.expects().returning(Future.successful(List(newStats)))

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.saveAll(Seq(
        toTopic(t1, Some(svg1), Some(stats.statistics)),
        toTopic(t2, Some(svg2), None)
      ))
      Some(existing) <- service.getByAlias(t1.alias)
    } yield existing

    //when
    val resultF = for {
      existing <- beforeF
      res <- service.fetch(refresh = true)
    } yield (existing, res)

    //then
    for {
      (existing, resList) <- resultF
      topicsList <- dao.list()
    } yield {
      resList shouldBe topicsList
      resList shouldBe Seq(
        existing.copy(stats = None),
        toTopic(newTopic, Some(newSvg), Some(newStats.statistics), id = 2)
      )
    }
  }
  
  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new TopicDao(ctx)
    val service = new TopicService(api.api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1", multiplier = 1)

    api.getTopics.expects().never()
    api.getTopicIcon.expects(*).never()
    api.getStats.expects().never()

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.saveAll(Seq(
        toTopic(t1, Some(svg1), Some(stats.statistics)),
        toTopic(t2, Some(svg2), None)
      ))
    } yield ()

    //when
    val resultF = for {
      _ <- beforeF
      res <- service.fetch()
    } yield res

    //then
    for {
      resList <- resultF
      topicsList <- dao.list()
    } yield {
      resList shouldBe topicsList
      resList shouldBe Seq(
        toTopic(t1, Some(svg1), Some(stats.statistics), id = 1),
        toTopic(t2, Some(svg2), None, id = 2)
      )
    }
  }
  
  private def getTopicData(alias: String): TopicWithInfoData = {
    TopicWithInfoData(
      alias = alias,
      name = "Test",
      language = "en",
      info = InfoData(
        numberOfQuestions = 1,
        numberOfPaid = 2,
        numberOfLearners = 3,
        numberOfChapters = 4,
        numberOfTheory = Some(5)
      )
    )
  }

  private def getStatsRespData(alias: String, multiplier: Int): StatsRespData = {
    StatsRespData(
      topic = StatsTopicData(
        alias = alias
      ),
      statistics = StatsData(
        progress = multiplier * 1,
        progressOnce = multiplier * 2,
        progressAll = multiplier * 3,
        freePercent = multiplier * 4,
        paid = multiplier * 5
      )
    )
  }

  private def toTopic(data: TopicWithInfoData,
                      maybeIcon: Option[String],
                      maybeStats: Option[StatsData],
                      id: Int = -1): Topic = {
    Topic(
      entity = TopicEntity(
        id = id,
        alias = data.alias,
        name = data.name,
        lang = data.language,
        numQuestions = data.info.numberOfQuestions,
        numPaid = data.info.numberOfPaid,
        numLearners = data.info.numberOfLearners,
        numChapters = data.info.numberOfChapters,
        numTheory = data.info.numberOfTheory,
        svgIcon = maybeIcon
      ),
      stats = maybeStats.map { stats =>
        TopicStats(
          id = id,
          progress = stats.progress,
          progressOnce = stats.progressOnce,
          progressAll = stats.progressAll,
          freePercent = stats.freePercent,
          paid = stats.paid
        )
      }
    )
  }
}
