package io.codegalaxy.app

import io.codegalaxy.domain._
import org.scalatest.Assertion
import scommons.nodejs.test.AsyncTestSpec
import scommons.websql.WebSQL
import scommons.websql.migrations.WebSqlMigrations

import scala.concurrent.Future

trait BaseDBContextSpec extends AsyncTestSpec {

  def withCtx(f: CodeGalaxyDBContext => Future[Assertion]): Future[Assertion] = {
    BaseDBContextSpec.contextF.flatMap(f)
  }
}

object BaseDBContextSpec {
  
  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val contextF: Future[CodeGalaxyDBContext] = {
    val db = WebSQL.openDatabase(":memory:")
    
    val migrations = new WebSqlMigrations(db)
    
    migrations.runBundle(CodeGalaxyDBMigrations).map { _ =>
      new CodeGalaxyDBContext(db)
    }
  }
}
