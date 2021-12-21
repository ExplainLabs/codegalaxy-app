package io.codegalaxy.domain

case class TopicStats(id: Int,
                      progress: Int,
                      progressOnce: Int,
                      progressAll: Int,
                      freePercent: Int,
                      paid: Int)
