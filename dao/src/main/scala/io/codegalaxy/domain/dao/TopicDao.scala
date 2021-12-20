package io.codegalaxy.domain.dao

import io.codegalaxy.domain._
import scommons.websql.io.dao.CommonDao

import scala.concurrent.Future

class TopicDao(val ctx: CodeGalaxyDBContext) extends CommonDao {

  import ctx._

  def getByAlias(alias: String): Future[Option[Topic]] = {
    val q = ctx.runQuery(
      sql =
        """SELECT
          |  x1.id            AS _0,
          |  x1.alias         AS _1,
          |  x1.name          AS _2,
          |  x1.lang          AS _3,
          |  x1.num_questions AS _4,
          |  x1.num_paid      AS _5,
          |  x1.num_learners  AS _6,
          |  x1.num_chapters  AS _7,
          |  x1.num_theory    AS _8,
          |  x1.svg_icon      AS _9,
          |  x2.id            AS _10,
          |  x2.progress      AS _11,
          |  x2.progress_once AS _12,
          |  x2.progress_all  AS _13,
          |  x2.free_percent  AS _14,
          |  x2.paid          AS _15
          |FROM
          |  topics x1
          |LEFT JOIN topics_stats x2
          |  ON x1.id = x2.id
          |WHERE
          |  x1.alias = ?
          |""".stripMargin,
      args = alias,
      extractor = {
        case (entity, maybeStats) =>
          Topic(TopicEntity.tupled(entity), maybeStats.map(TopicStats.tupled))
      }: (((Int, String, String, String, Int, Int, Int, Int, Option[Int], Option[String]), Option[(Int, Int, Int, Int, Int, Int)])) =>
        Topic
    )
    
    getOne("getByAlias", ctx.performIO(q))
  }

  def list(): Future[Seq[Topic]] = {
    ctx.performIO(listQuery())
  }

  private def listQuery(): IO[Seq[Topic], Effect.Read] = {
    ctx.runQuery(
      sql =
        """SELECT
          |  x3.id            AS _0,
          |  x3.alias         AS _1,
          |  x3.name          AS _2,
          |  x3.lang          AS _3,
          |  x3.num_questions AS _4,
          |  x3.num_paid      AS _5,
          |  x3.num_learners  AS _6,
          |  x3.num_chapters  AS _7,
          |  x3.num_theory    AS _8,
          |  x3.svg_icon      AS _9,
          |  x4.id            AS _10,
          |  x4.progress      AS _11,
          |  x4.progress_once AS _12,
          |  x4.progress_all  AS _13,
          |  x4.free_percent  AS _14,
          |  x4.paid          AS _15
          |FROM
          |  topics x3
          |LEFT JOIN topics_stats x4
          |  ON x3.id = x4.id
          |ORDER BY
          |  x3.id
          |""".stripMargin,
      extractor = {
        case (entity, maybeStats) =>
          Topic(TopicEntity.tupled(entity), maybeStats.map(TopicStats.tupled))
      }: (((Int, String, String, String, Int, Int, Int, Int, Option[Int], Option[String]), Option[(Int, Int, Int, Int, Int, Int)])) =>
        Topic
    )
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
      ids <- ctx.runQuery("SELECT id FROM topics WHERE alias = ?", alias, identity[Int])
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
      updatedCount <- ctx.runAction(
        sql =
          """UPDATE topics_stats SET
            |  progress = ?, progress_once = ?, progress_all = ?, free_percent = ?, paid = ?
            |WHERE
            |  id = ?
            |""".stripMargin,
        args = (stats.progress, stats.progressOnce, stats.progressAll, stats.freePercent, stats.paid, stats.id)
      )
      _ <-
        if (updatedCount == 0) ctx.runActionReturning(
          sql =
            """INSERT INTO topics_stats
              |  (id, progress, progress_once, progress_all, free_percent, paid)
              |VALUES
              |  (?, ?, ?, ?, ?, ?)
              |""".stripMargin,
          args = (stats.id, stats.progress, stats.progressOnce, stats.progressAll, stats.freePercent, stats.paid)
        )
        else ctx.IO.successful(())
    } yield stats
  }
  
  private def insertTopicsAction(list: Seq[TopicEntity]): IO[Seq[Int], Effect.Write] = {
    IO.sequence(list.map { t =>
      ctx.runActionReturning(
        sql =
          """INSERT INTO topics
            |  (alias, name, lang, num_questions, num_paid, num_learners, num_chapters, num_theory, svg_icon)
            |VALUES
            |  (?, ?, ?, ?, ?, ?, ?, ?, ?)
            |""".stripMargin,
        args = (t.alias, t.name, t.lang, t.numQuestions, t.numPaid, t.numLearners, t.numChapters, t.numTheory, t.svgIcon)
      )
    }).map(_.map(_.toInt))
  }

  private def insertStatsAction(list: Seq[TopicStats]): IO[Seq[Long], Effect.Write] = {
    IO.sequence(list.map { s =>
      ctx.runActionReturning(
        sql =
          """INSERT INTO topics_stats
            |  (id, progress, progress_once, progress_all, free_percent, paid)
            |VALUES
            |  (?, ?, ?, ?, ?, ?)
            |""".stripMargin,
        args = (s.id, s.progress, s.progressOnce, s.progressAll, s.freePercent, s.paid)
      )
    })
  }

  def deleteAll(): Future[Unit] = {
    ctx.performIO(deleteAllAction())
  }

  private def deleteAllAction(): IO[Unit, Effect.Write] = {
    for {
      _ <- ctx.runAction("DELETE FROM topics_stats")
      _ <- ctx.runAction("DELETE FROM topics")
    } yield ()
  }
}
