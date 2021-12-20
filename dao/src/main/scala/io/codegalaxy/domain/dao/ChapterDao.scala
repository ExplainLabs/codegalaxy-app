package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.io.dao.CommonDao

import scala.concurrent.Future

class ChapterDao(val ctx: CodeGalaxyDBContext) extends CommonDao {

  import ctx._

  def getByAlias(topic: String, alias: String): Future[Option[Chapter]] = {
    val q = ctx.runQuery(
      sql =
        """SELECT
          |  x1.id            AS _0,
          |  x1.topic         AS _1,
          |  x1.alias         AS _2,
          |  x1.name          AS _3,
          |  x1.num_questions AS _4,
          |  x1.num_paid      AS _5,
          |  x1.num_learners  AS _6,
          |  x1.num_chapters  AS _7,
          |  x1.num_theory    AS _8,
          |  x2.id            AS _9,
          |  x2.progress      AS _10,
          |  x2.progress_once AS _11,
          |  x2.progress_all  AS _12,
          |  x2.free_percent  AS _13,
          |  x2.paid          AS _14
          |FROM
          |  chapters x1
          |LEFT JOIN chapters_stats x2
          |  ON x1.id = x2.id
          |WHERE
          |  x1.topic = ? AND x1.alias = ?
          |""".stripMargin,
      args = (topic, alias),
      extractor = {
        case (entity, maybeStats) =>
          Chapter(ChapterEntity.tupled(entity), maybeStats.map(ChapterStats.tupled))
      }: (((Int, String, String, String, Int, Int, Int, Int, Option[Int]), Option[(Int, Int, Int, Int, Int, Int)])) =>
        Chapter
    )
    
    getOne("getByAlias", ctx.performIO(q))
  }

  def list(topic: String): Future[Seq[Chapter]] = {
    ctx.performIO(listQuery(topic))
  }

  private def listQuery(topic: String): IO[Seq[Chapter], Effect.Read] = {
    ctx.runQuery(
      sql =
        """SELECT
          |  x3.id            AS _0,
          |  x3.topic         AS _1,
          |  x3.alias         AS _2,
          |  x3.name          AS _3,
          |  x3.num_questions AS _4,
          |  x3.num_paid      AS _5,
          |  x3.num_learners  AS _6,
          |  x3.num_chapters  AS _7,
          |  x3.num_theory    AS _8,
          |  x4.id            AS _9,
          |  x4.progress      AS _10,
          |  x4.progress_once AS _11,
          |  x4.progress_all  AS _12,
          |  x4.free_percent  AS _13,
          |  x4.paid          AS _14
          |FROM
          |  chapters x3
          |LEFT JOIN chapters_stats x4
          |  ON x3.id = x4.id
          |WHERE x3.topic = ?
          |ORDER BY
          |  x3.id
          |""".stripMargin,
      args = topic,
      extractor = {
        case (entity, maybeStats) =>
          Chapter(ChapterEntity.tupled(entity), maybeStats.map(ChapterStats.tupled))
      }: (((Int, String, String, String, Int, Int, Int, Int, Option[Int]), Option[(Int, Int, Int, Int, Int, Int)])) =>
        Chapter
    )
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
      ids <- ctx.runQuery(
        sql = "SELECT id FROM chapters WHERE topic = ? AND alias = ?",
        args = (topic, alias),
        extractor = identity[Int]
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
      updatedCount <- ctx.runAction(
        sql =
          """UPDATE
            |  chapters_stats
            |SET
            |  progress = ?, progress_once = ?, progress_all = ?, free_percent = ?, paid = ?
            |WHERE
            |  id = ?
            |""".stripMargin,
        args = (stats.progress, stats.progressOnce, stats.progressAll, stats.freePercent, stats.paid, stats.id)
      )
      _ <-
        if (updatedCount == 0) ctx.runActionReturning(
          sql =
            """INSERT INTO chapters_stats
              |  (id, progress, progress_once, progress_all, free_percent, paid)
              |VALUES
              |  (?, ?, ?, ?, ?, ?)
              |""".stripMargin,
          args = (stats.id, stats.progress, stats.progressOnce, stats.progressAll, stats.freePercent, stats.paid)
        )
        else ctx.IO.successful(())
    } yield stats
  }

  private def getExistingIdsQuery(topic: String): IO[Seq[Int], Effect.Read] = {
    ctx.runQuery("SELECT id FROM chapters WHERE topic = ?", topic, identity[Int])
  }
  
  private def insertChaptersAction(list: Seq[ChapterEntity]): IO[Seq[Int], Effect.Write] = {
    IO.sequence(list.map { c =>
      ctx.runActionReturning(
        sql =
          """INSERT INTO chapters
            |  (topic, alias, name, num_questions, num_paid, num_learners, num_chapters, num_theory)
            |VALUES
            |  (?, ?, ?, ?, ?, ?, ?, ?)
            |""".stripMargin,
        args = (c.topic, c.alias, c.name, c.numQuestions, c.numPaid, c.numLearners, c.numChapters, c.numTheory)
      )
    }).map(_.map(_.toInt))
  }

  private def insertStatsAction(list: Seq[ChapterStats]): IO[Seq[Long], Effect.Write] = {
    IO.sequence(list.map { s =>
      ctx.runAction(
        sql =
          """INSERT INTO chapters_stats
            |  (id, progress, progress_once, progress_all, free_percent, paid)
            |VALUES
            |  (?, ?, ?, ?, ?, ?)
            |""".stripMargin,
        args = (s.id, s.progress, s.progressOnce, s.progressAll, s.freePercent, s.paid)
      )
    })
  }

  private def deleteChaptersAction(ids: Seq[Int]): IO[Long, Effect.Write] = {
    ctx.runAction(s"DELETE FROM chapters WHERE id IN (${ids.map(_ => "?").mkString(",")})", ids)
  }
  
  private def deleteStatsAction(ids: Seq[Int]): IO[Long, Effect.Write] = {
    ctx.runAction(s"DELETE FROM chapters_stats WHERE id IN (${ids.map(_ => "?").mkString(",")})", ids)
  }

  def deleteAll(): Future[Unit] = {
    val q = for {
      _ <- ctx.runAction("DELETE FROM chapters_stats")
      _ <- ctx.runAction("DELETE FROM chapters")
    } yield ()

    ctx.performIO(q)
  }
}
