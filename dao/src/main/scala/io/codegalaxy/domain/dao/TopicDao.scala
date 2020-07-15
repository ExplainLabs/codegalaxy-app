package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class TopicDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with TopicSchema {

  import ctx._

  def getById(id: Int): Future[Option[TopicEntity]] = {
    getOne("getById", ctx.performIO(ctx.run(topics
      .filter(c => c.id == lift(id))
    )))
  }

  def list(): Future[Seq[TopicEntity]] = {
    ctx.performIO(listQuery())
  }

  private def listQuery(): IO[Seq[TopicEntity], Effect.Read] = {
    ctx.run(topics
      .sortBy(_.id)
    )
  }

  def upsertMany(data: Seq[TopicEntity]): Future[Seq[TopicEntity]] = {
    val q = for {
      existing <- getExistingQuery(data.map(_.alias).toSet)
      (toUpdate, toInsert) = {
        data.map { t =>
          val id = existing.find(_._2 == t.alias).map(_._1).getOrElse(-1)
          t.copy(id = id)
        }.partition(_.id != -1)
      }
      _ <- updateManyAction(toUpdate)
      _ <- insertManyAction(toInsert)
      res <- listQuery()
    } yield res
    
    ctx.performIO(q)
  }

  private def getExistingQuery(aliases: Set[String]): IO[Seq[(Int, String)], Effect.Read] = {
    ctx.run(topics
      .filter(t => liftQuery(aliases).contains(t.alias))
      .map(t => (t.id, t.alias))
    )
  }
  
  private def insertManyAction(list: Seq[TopicEntity]): IO[Seq[Int], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topics
          .insert(entity)
          .returning(_.id)
      }
    }

    ctx.run(q).map(_.map(_.toInt))
  }

  private def updateManyAction(list: Seq[TopicEntity]): IO[Seq[Boolean], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topics
          .filter(_.id == entity.id)
          .update(entity)
      }
    }

    ctx.run(q).map { results =>
      results.map(_ > 0)
    }
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.run(topics.delete))
  }
}
