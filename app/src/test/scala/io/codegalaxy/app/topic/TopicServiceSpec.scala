package io.codegalaxy.app.topic

import io.codegalaxy.api.data.InfoData
import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.app.topic.TopicServiceSpec._
import io.codegalaxy.domain.TopicEntity
import io.codegalaxy.domain.dao.TopicDao

import scala.concurrent.Future

class TopicServiceSpec extends BaseDBContextSpec {

  it should "fetch topics and save them in DB" in withCtx { ctx =>
    //given
    val api = mock[TopicWithStatsApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1")

    (api.getTopics _).expects().returning(Future.successful(List(t1, t2)))
    (api.getTopicIcon _).expects(t1.alias).returning(Future.successful(Some(svg1)))
    (api.getTopicIcon _).expects(t2.alias).returning(Future.successful(Some(svg2)))
    (api.getStats _).expects().returning(Future.successful(List(stats)))

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
        toTopicEntity(t1, Some(svg1), Some(stats.statistics.progressAll)).copy(id = 1),
        toTopicEntity(t2, Some(svg2), None).copy(id = 2)
      )
    }
  }
  
  it should "refresh topics in DB" in withCtx { ctx =>
    //given
    val api = mock[TopicWithStatsApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1")
    val newTopic = getTopicData("newTopic")
    val newSvg = "new svg"
    val newStats = getStatsRespData("newTopic")

    (api.getTopics _).expects().returning(Future.successful(List(t1, newTopic)))
    (api.getTopicIcon _).expects(t1.alias).returning(Future.successful(Some(svg1)))
    (api.getTopicIcon _).expects(newTopic.alias).returning(Future.successful(Some(newSvg)))
    (api.getStats _).expects().returning(Future.successful(List(newStats)))

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(Seq(
        toTopicEntity(t1, Some(svg1), Some(stats.statistics.progressAll)),
        toTopicEntity(t2, Some(svg2), None)
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
        existing.copy(progress = None),
        toTopicEntity(newTopic, Some(newSvg), Some(newStats.statistics.progressAll)).copy(id = 3)
      )
    }
  }
  
  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = mock[TopicWithStatsApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val stats = getStatsRespData("topic1")

    (api.getTopics _).expects().never()
    (api.getTopicIcon _).expects(*).never()
    (api.getStats _).expects().never()

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(Seq(
        toTopicEntity(t1, Some(svg1), Some(stats.statistics.progressAll)),
        toTopicEntity(t2, Some(svg2), None)
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
        toTopicEntity(t1, Some(svg1), Some(stats.statistics.progressAll)).copy(id = 1),
        toTopicEntity(t2, Some(svg2), None).copy(id = 2)
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
        numberOfChapters = 4
      )
    )
  }

  private def getStatsRespData(alias: String): StatsRespData = {
    StatsRespData(
      topic = StatsTopicData(
        alias = alias
      ),
      statistics = StatsData(
        progressAll = 88
      )
    )
  }

  private def toTopicEntity(data: TopicWithInfoData,
                            maybeIcon: Option[String],
                            maybeProgress: Option[Int]): TopicEntity = {
    TopicEntity(
      id = -1,
      alias = data.alias,
      name = data.name,
      lang = data.language,
      numQuestions = data.info.numberOfQuestions,
      numPaid = data.info.numberOfPaid,
      numLearners = data.info.numberOfLearners,
      numChapters = data.info.numberOfChapters,
      svgIcon = maybeIcon,
      progress = maybeProgress
    )
  }
}

object TopicServiceSpec {

  trait TopicWithStatsApi extends TopicApi with StatsApi
}
