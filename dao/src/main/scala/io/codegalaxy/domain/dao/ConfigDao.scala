package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class ConfigDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with ConfigSchema {

  import ctx._

  def getByUserId(userId: Int): Future[Option[ConfigEntity]] = {
    getOne("getByUserId", ctx.performIO(getByUserIdQuery(userId)))
  }

  def save(entity: ConfigEntity): Future[ConfigEntity] = {
    val q = for {
      maybeExisting <- getByUserIdQuery(entity.userId).map(_.headOption)
      _ <- maybeExisting match {
        case None => insertQuery(entity)
        case Some(_) => updateQuery(entity)
      }
      res <- getByUserIdQuery(entity.userId).map(_.head)
    } yield res

    ctx.performIO(q)
  }

  private def getByUserIdQuery(userId: Int): IO[Seq[ConfigEntity], Effect.Read] = {
    ctx.run(configs
      .filter(c => c.userId == lift(userId))
    )
  }

  private def insertQuery(entity: ConfigEntity): IO[Long, Effect.Write] = {
    ctx.run(configs
      .insert(lift(entity))
    )
  }

  private def updateQuery(entity: ConfigEntity): IO[Long, Effect.Write] = {
    ctx.run(configs
      .filter(c => c.userId == lift(entity.userId))
      .update(lift(entity))
    )
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.run(configs.delete))
  }
}
