package io.codegalaxy.domain

case class ChapterStats(id: Int,
                        progress: Int,
                        progressOnce: Int,
                        progressAll: Int,
                        freePercent: Int,
                        paid: Int)
