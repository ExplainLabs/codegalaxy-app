package io.codegalaxy.app.topic

import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.dao.TopicDao

class TopicServiceSpec extends BaseDBContextSpec {

  it should "fetch topics and save them in DB" in withCtx { ctx =>
    //given
    val dao = new TopicDao(ctx)
    val service = new TopicService(dao)

    //when
    val result = service.getById(1)

    //then
    result.map { res =>
      res shouldBe None
    }
  }
}
