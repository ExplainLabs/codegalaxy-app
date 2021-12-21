package io.codegalaxy.domain

case class ChapterEntity(id: Int,
                         topic: String,
                         alias: String,
                         name: String,
                         numQuestions: Int,
                         numPaid: Int,
                         numLearners: Int,
                         numChapters: Int,
                         numTheory: Option[Int])
