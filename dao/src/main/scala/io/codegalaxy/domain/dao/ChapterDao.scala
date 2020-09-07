package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class ChapterDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with ChapterSchema {

  import ctx._

  def getByAlias(topic: String, alias: String): Future[Option[ChapterEntity]] = {
    getOne("getByAlias", ctx.performIO(ctx.run(chapters
      .filter { r =>
        r.topic == lift(topic) &&
          r.alias == lift(alias)
      }
    )))
  }

  def list(topic: String): Future[Seq[ChapterEntity]] = {
    ctx.performIO(listQuery(topic))
  }

  private def listQuery(topic: String): IO[Seq[ChapterEntity], Effect.Read] = {
    ctx.run(chapters
      .filter(r => r.topic == lift(topic))
      .sortBy(_.id)
    )
  }

  def upsertMany(topic: String, data: Seq[ChapterEntity]): Future[Seq[ChapterEntity]] = {
    val q = for {
      existing <- getExistingQuery(topic)
      (toUpdate, toInsert, idsToDelete) = {
        val (toUpdate, toInsert) = data.map { r =>
          val id = existing.find(_._2 == r.alias).map(_._1).getOrElse(-1)
          r.copy(id = id)
        }.partition(_.id != -1)
        
        (toUpdate, toInsert, existing.collect {
          case r if !toUpdate.exists(_.id == r._1) => r._1
        })
      }
      _ <- updateManyAction(toUpdate)
      _ <- insertManyAction(toInsert)
      _ <- deleteManyAction(idsToDelete)
      res <- listQuery(topic)
    } yield res
    
    ctx.performIO(q)
  }

  private def getExistingQuery(topic: String): IO[Seq[(Int, String)], Effect.Read] = {
    ctx.run(chapters
      .filter(r => r.topic == lift(topic))
      .map(r => (r.id, r.alias))
    )
  }
  
  private def insertManyAction(list: Seq[ChapterEntity]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        chapters
          .insert(entity)
          .returning(_.id)
      }
    }

    ctx.run(q)
  }

  private def updateManyAction(list: Seq[ChapterEntity]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        chapters
          .filter(_.id == entity.id)
          .update(entity)
      }
    }

    ctx.run(q)
  }

  private def deleteManyAction(ids: Seq[Int]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(ids).foreach { id =>
        chapters
          .filter(_.id == id)
          .delete
      }
    }

    ctx.run(q)
  }

  def deleteAll(): Future[Long] = {
    ctx.performIO(ctx.run(chapters.delete))
  }
}
