package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class TopicDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with TopicSchema {

  import ctx._

  def getByAlias(alias: String): Future[Option[TopicEntity]] = {
    getOne("getByAlias", ctx.performIO(ctx.run(topics
      .filter(c => c.alias == lift(alias))
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
      existing <- getExistingQuery
      (toUpdate, toInsert, idsToDelete) = {
        val (toUpdate, toInsert) = data.map { t =>
          val id = existing.find(_._2 == t.alias).map(_._1).getOrElse(-1)
          t.copy(id = id)
        }.partition(_.id != -1)
        
        (toUpdate, toInsert, existing.collect {
          case t if !toUpdate.exists(_.id == t._1) => t._1
        })
      }
      _ <- updateManyAction(toUpdate)
      _ <- insertManyAction(toInsert)
      _ <- deleteManyAction(idsToDelete)
      res <- listQuery()
    } yield res
    
    ctx.performIO(q)
  }

  private def getExistingQuery: IO[Seq[(Int, String)], Effect.Read] = {
    ctx.run(topics
      .map(t => (t.id, t.alias))
    )
  }
  
  private def insertManyAction(list: Seq[TopicEntity]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topics
          .insert(entity)
          .returning(_.id)
      }
    }

    ctx.run(q)
  }

  private def updateManyAction(list: Seq[TopicEntity]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topics
          .filter(_.id == entity.id)
          .update(entity)
      }
    }

    ctx.run(q)
  }

  private def deleteManyAction(ids: Seq[Int]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(ids).foreach { id =>
        topics
          .filter(_.id == id)
          .delete
      }
    }

    ctx.run(q)
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.run(topics.delete))
  }
}
