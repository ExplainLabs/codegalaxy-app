package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.quill.dao.CommonDao

import scala.concurrent.Future

class TopicDao(val ctx: CodeGalaxyDBContext) extends CommonDao
  with TopicSchema
  with TopicStatsSchema {

  import ctx._

  def getByAlias(alias: String): Future[Option[Topic]] = {
    val q = for {
      results <- ctx.run(topics.leftJoin(topicsStats).on(_.id == _.id)
        .filter { case (c, _) => c.alias == lift(alias) }
      )
    } yield {
      results.map { case (entity, maybeStats) =>
        Topic(entity, maybeStats)
      }
    }
    
    getOne("getByAlias", ctx.performIO(q))
  }

  def list(): Future[Seq[Topic]] = {
    ctx.performIO(listQuery())
  }

  private def listQuery(): IO[Seq[Topic], Effect.Read] = {
    for {
      topics <- ctx.run(topics.leftJoin(topicsStats).on(_.id == _.id)
        .sortBy { case (r, _) => r.id }
      )
    } yield {
      topics.map { case (entity, maybeStats) =>
        Topic(entity, maybeStats)
      }
    }
  }

  def saveAll(data: Seq[Topic]): Future[Seq[Topic]] = {
    val q = for {
      _ <- deleteAllAction()
      ids <- insertTopicsAction(data.map(_.entity))
      topicsStats = data.zip(ids).flatMap { case (Topic(_, stats), id) =>
        stats.map(_.copy(id = id))
      }
      _ <- insertStatsAction(topicsStats)
      res <- listQuery()
    } yield res
    
    ctx.performIO(q)
  }

  def saveStats(alias: String, stats: TopicStats): Future[Option[TopicStats]] = {
    val q = for {
      ids <- ctx.run(topics
        .filter(_.alias == lift(alias))
        .map(r => r.id)
      )
      res <- ids.headOption match {
        case None => ctx.IO.successful(None)
        case Some(topicId) =>
          saveStatsAction(stats.copy(id = topicId))
            .map(Some(_))
      }
    } yield res

    ctx.performIO(q)
  }
  
  private def saveStatsAction(stats: TopicStats): IO[TopicStats, Effect.Write] = {
    for {
      updatedCount <- ctx.run(topicsStats
        .filter(_.id == lift(stats.id))
        .update(lift(stats))
      )
      _ <-
        if (updatedCount == 0) ctx.run(topicsStats.insert(lift(stats)))
        else ctx.IO.successful(())
    } yield stats
  }
  
  private def insertTopicsAction(list: Seq[TopicEntity]): IO[Seq[Int], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topics
          .insert(entity)
          .returning(_.id)
      }
    }

    ctx.run(q).map(_.map(_.toInt))
  }

  private def insertStatsAction(list: Seq[TopicStats]): IO[Seq[Long], Effect.Write] = {
    val q = quote {
      liftQuery(list).foreach { entity =>
        topicsStats.insert(entity)
      }
    }

    ctx.run(q)
  }

  def deleteAll(): Future[Unit] = {
    ctx.performIO(deleteAllAction())
  }

  private def deleteAllAction(): IO[Unit, Effect.Write] = {
    for {
      _ <- ctx.run(topicsStats.delete)
      _ <- ctx.run(topics.delete)
    } yield ()
  }
}
