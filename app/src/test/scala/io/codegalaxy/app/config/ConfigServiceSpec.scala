package io.codegalaxy.app.config

import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.ConfigEntity
import io.codegalaxy.domain.dao.ConfigDao

class ConfigServiceSpec extends BaseDBContextSpec {

  it should "create new config when setDarkTheme" in withCtx { ctx =>
    //given
    val dao = new ConfigDao(ctx)
    val service = new ConfigService(dao)
    val userId = 123
    
    val beforeF = dao.deleteAll()
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.setDarkTheme(userId, darkTheme = true)
    }

    //then
    for {
      res <- resultF
      Some(curr) <- service.getConfig(userId)
    } yield {
      res shouldBe curr
      res shouldBe ConfigEntity(userId, darkTheme = true)
    }
  }
  
  it should "update existing config when setDarkTheme" in withCtx { ctx =>
    //given
    val dao = new ConfigDao(ctx)
    val service = new ConfigService(dao)
    val userId = 123
    
    val beforeF = dao.save(ConfigEntity(userId, darkTheme = true))
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.setDarkTheme(userId, darkTheme = false)
    }

    //then
    for {
      res <- resultF
      Some(curr) <- service.getConfig(userId)
    } yield {
      res shouldBe curr
      res shouldBe ConfigEntity(userId, darkTheme = false)
    }
  }
}
