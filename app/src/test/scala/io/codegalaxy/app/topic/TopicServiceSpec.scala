package io.codegalaxy.app.topic

import io.codegalaxy.api.topic._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.TopicEntity
import io.codegalaxy.domain.dao.TopicDao

import scala.concurrent.Future

class TopicServiceSpec extends BaseDBContextSpec {

  it should "fetch topics and save them in DB" in withCtx { ctx =>
    //given
    val api = mock[TopicApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")

    (api.getTopics _).expects(true).returning(Future.successful(List(t1, t2)))
    (api.getTopicIcon _).expects(t1.alias).returning(Future.successful(Some(svg1)))
    (api.getTopicIcon _).expects(t2.alias).returning(Future.successful(Some(svg2)))

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
        toTopicEntity(t1, Some(svg1)).copy(id = 1),
        toTopicEntity(t2, Some(svg2)).copy(id = 2)
      )
    }
  }
  
  it should "refresh topics in DB" in withCtx { ctx =>
    //given
    val api = mock[TopicApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")
    val newTopic = getTopicData("newTopic")
    val newSvg = "new svg"

    (api.getTopics _).expects(true).returning(Future.successful(List(t1, newTopic)))
    (api.getTopicIcon _).expects(t1.alias).returning(Future.successful(Some(svg1)))
    (api.getTopicIcon _).expects(newTopic.alias).returning(Future.successful(Some(newSvg)))

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(Seq(
        toTopicEntity(t1, Some(svg1)),
        toTopicEntity(t2, Some(svg2))
      ))
      Some(existing) <- service.getById(1)
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
        existing,
        toTopicEntity(newTopic, Some(newSvg)).copy(id = 3)
      )
    }
  }
  
  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = mock[TopicApi]
    val dao = new TopicDao(ctx)
    val service = new TopicService(api, dao)
    val (t1, svg1) = (getTopicData("topic1"), "test svg1")
    val (t2, svg2) = (getTopicData("topic2"), "test svg2")

    (api.getTopics _).expects(*).never()
    (api.getTopicIcon _).expects(*).never()

    val beforeF = for {
      _ <- dao.deleteAll()
      _ <- dao.upsertMany(Seq(
        toTopicEntity(t1, Some(svg1)),
        toTopicEntity(t2, Some(svg2))
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
        toTopicEntity(t1, Some(svg1)).copy(id = 1),
        toTopicEntity(t2, Some(svg2)).copy(id = 2)
      )
    }
  }
  
  private def getTopicData(alias: String): TopicWithInfoData = {
    TopicWithInfoData(
      alias = alias,
      name = "Test",
      language = "en",
      info = Some(TopicInfoData(
        numberOfQuestions = 1,
        numberOfPaid = 2,
        numberOfLearners = 3,
        numberOfChapters = 4
      ))
    )
  }

  private def toTopicEntity(data: TopicWithInfoData,
                            maybeIcon: Option[String]): TopicEntity = {
    TopicEntity(
      id = -1,
      alias = data.alias,
      name = data.name,
      lang = data.language,
      numQuestions = data.info.map(_.numberOfQuestions).getOrElse(0),
      numPaid = data.info.map(_.numberOfPaid).getOrElse(0),
      numLearners = data.info.map(_.numberOfLearners).getOrElse(0),
      numChapters = data.info.map(_.numberOfChapters).getOrElse(0),
      svgIcon = maybeIcon
    )
  }
}
