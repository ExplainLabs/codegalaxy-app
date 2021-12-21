package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.io.dao.CommonDao

import scala.concurrent.Future

class ConfigDao(val ctx: CodeGalaxyDBContext) extends CommonDao {

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
    ctx.runQuery(
      sql = "SELECT c.user_id, c.dark_theme FROM configs c WHERE c.user_id = ?",
      args = userId,
      extractor = ConfigEntity.tupled
    )
  }

  private def insertQuery(entity: ConfigEntity): IO[Long, Effect.Write] = {
    ctx.runActionReturning(
      sql = "INSERT INTO configs (user_id, dark_theme) VALUES (?, ?)",
      args = (entity.userId, entity.darkTheme)
    )
  }

  private def updateQuery(entity: ConfigEntity): IO[Long, Effect.Write] = {
    ctx.runAction(
      sql = "UPDATE configs SET dark_theme = ? WHERE user_id = ?",
      args = (entity.darkTheme, entity.userId)
    )
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.runAction("DELETE FROM configs"))
  }
}
