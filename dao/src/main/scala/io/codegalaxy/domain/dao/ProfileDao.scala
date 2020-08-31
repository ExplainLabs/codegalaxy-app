package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class ProfileDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with ProfileSchema {

  import ctx._

  def getCurrent: Future[Option[ProfileEntity]] = {
    getOne("getCurrent", ctx.performIO(ctx.run(profiles)))
  }

  def insert(entity: ProfileEntity): Future[ProfileEntity] = {
    val q = for {
      _ <- insertQuery(entity)
      res <- getByIdQuery(entity.id).map(_.head)
    } yield res

    ctx.performIO(q)
  }

  private def getByIdQuery(id: Int): IO[Seq[ProfileEntity], Effect.Read] = {
    ctx.run(profiles
      .filter(c => c.id == lift(id))
    )
  }

  private def insertQuery(entity: ProfileEntity): IO[Long, Effect.Write] = {
    ctx.run(profiles
      .insert(lift(entity))
    )
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.run(profiles.delete))
  }
}
