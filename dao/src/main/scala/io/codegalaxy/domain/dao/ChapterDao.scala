package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class ChapterDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with ChapterSchema
  with ChapterStatsSchema {

  import ctx._

  def getByAlias(topic: String, alias: String): Future[Option[Chapter]] = {
    val q = for {
      results <- ctx.run(chapters.leftJoin(chaptersStats).on(_.id == _.id)
        .filter { case (r, _) =>
          r.topic == lift(topic) &&
            r.alias == lift(alias)
        }
      )
    } yield {
      results.map { case (entity, maybeStats) =>
        Chapter(entity, maybeStats)
      }
    }
    
    getOne("getByAlias", ctx.performIO(q))
  }

  def list(topic: String): Future[Seq[Chapter]] = {
    ctx.performIO(listQuery(topic))
  }

  private def listQuery(topic: String): IO[Seq[Chapter], Effect.Read] = {
    for {
      results <- ctx.run(chapters.leftJoin(chaptersStats).on(_.id == _.id)
        .filter { case (r, _) =>
          r.topic == lift(topic)
        }
        .sortBy { case (r, _) => r.id }
      )
    } yield {
      results.map { case (entity, maybeStats) =>
        Chapter(entity, maybeStats)
      }
    }
  }

  def saveAll(topic: String, data: Seq[Chapter]): Future[Seq[Chapter]] = {
    val q = for {
      idsToDelete <- getExistingIdsQuery(topic)
      _ <- deleteStatsAction(idsToDelete)
      _ <- deleteChaptersAction(idsToDelete)
      ids <- insertChaptersAction(data.map(_.entity))
      topicsStats = data.zip(ids).flatMap { case (Chapter(_, stats), id) =>
        stats.map(_.copy(id = id))
      }
      _ <- insertStatsAction(topicsStats)
      res <- listQuery(topic)
    } yield res
    
    ctx.performIO(q)
  }

  def saveStats(topic: String, alias: String, stats: ChapterStats): Future[Option[ChapterStats]] = {
    val q = for {
      ids <- ctx.run(chapters
        .filter { r =>
          r.topic == lift(topic) &&
            r.alias == lift(alias)
        }
        .map(r => r.id)
      )
      res <- ids.headOption match {
        case None => ctx.IO.successful(None)
        case Some(chapterId) =>
          saveStatsAction(stats.copy(id = chapterId))
            .map(Some(_))
      }
    } yield res

    ctx.performIO(q)
  }

  private def saveStatsAction(stats: ChapterStats): IO[ChapterStats, Effect.Write] = {
    for {
      updatedCount <- ctx.run(chaptersStats
        .filter(_.id == lift(stats.id))
        .update(lift(stats))
      )
      _ <-
        if (updatedCount == 0) ctx.run(chaptersStats.insert(lift(stats)))
        else ctx.IO.successful(())
    } yield stats
  }

  private def getExistingIdsQuery(topic: String): IO[Seq[Int], Effect.Read] = {
    ctx.run(chapters
      .filter(r => r.topic == lift(topic))
      .map(r => r.id)
    )
  }
  
  private def insertChaptersAction(list: Seq[ChapterEntity]): IO[Seq[Int], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        chapters
          .insert(entity)
          .returning(_.id)
      }
    }

    ctx.run(q).map(_.map(_.toInt))
  }

  private def insertStatsAction(list: Seq[ChapterStats]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        chaptersStats.insert(entity)
      }
    }

    ctx.run(q)
  }

  private def deleteChaptersAction(ids: Seq[Int]): IO[Long, Effect.Write] = {
    ctx.run(chapters
      .filter(c => liftQuery(ids).contains(c.id))
      .delete
    )
  }
  
  private def deleteStatsAction(ids: Seq[Int]): IO[Long, Effect.Write] = {
    ctx.run(chaptersStats
      .filter(c => liftQuery(ids).contains(c.id))
      .delete
    )
  }

  def deleteAll(): Future[Unit] = {
    val q = for {
      _ <- ctx.run(chaptersStats.delete)
      _ <- ctx.run(chapters.delete)
    } yield ()

    ctx.performIO(q)
  }
}
