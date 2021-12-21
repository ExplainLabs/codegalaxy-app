package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.io.dao.CommonDao

import scala.concurrent.Future

class ProfileDao(val ctx: CodeGalaxyDBContext) extends CommonDao {

  import ctx._

  def getCurrent: Future[Option[ProfileEntity]] = {
    val q = ctx.runQuery(
      sql =
        """SELECT
          |  x.id,
          |  x.username,
          |  x.email,
          |  x.first_name,
          |  x.last_name,
          |  x.full_name,
          |  x.city,
          |  x.avatar_url
          |FROM
          |  profiles x
          |""".stripMargin,
      extractor = ProfileEntity.tupled
    )

    getOne("getCurrent", ctx.performIO(q))
  }

  def insert(entity: ProfileEntity): Future[ProfileEntity] = {
    val q = for {
      _ <- insertQuery(entity)
      res <- getByIdQuery(entity.id).map(_.head)
    } yield res

    ctx.performIO(q)
  }

  private def getByIdQuery(id: Int): IO[Seq[ProfileEntity], Effect.Read] = {
    ctx.runQuery(
      sql =
        """SELECT
          |  c.id,
          |  c.username,
          |  c.email,
          |  c.first_name,
          |  c.last_name,
          |  c.full_name,
          |  c.city,
          |  c.avatar_url
          |FROM
          |  profiles c
          |WHERE
          |  c.id = ?
          |""".stripMargin,
      args = id,
      extractor = ProfileEntity.tupled
    )
  }

  private def insertQuery(p: ProfileEntity): IO[Long, Effect.Write] = {
    ctx.runActionReturning(
      sql =
        """INSERT INTO profiles
          |  (id, username, email, first_name, last_name, full_name, city, avatar_url)
          |VALUES
          |  (?, ?, ?, ?, ?, ?, ?, ?)
          |""".stripMargin,
      args = (p.id, p.username, p.email, p.firstName, p.lastName, p.fullName, p.city, p.avatarUrl)
    )
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.runAction("DELETE FROM profiles"))
  }
}
