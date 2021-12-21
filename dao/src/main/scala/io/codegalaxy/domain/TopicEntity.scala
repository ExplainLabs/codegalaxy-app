package io.codegalaxy.domain

case class TopicEntity(id: Int,
                       alias: String,
                       name: String,
                       lang: String,
                       numQuestions: Int,
                       numPaid: Int,
                       numLearners: Int,
                       numChapters: Int,
                       numTheory: Option[Int],
                       svgIcon: Option[String])
