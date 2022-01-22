package io.codegalaxy.app.topic

import io.codegalaxy.domain.Topic

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockTopicService(
  fetchMock: Boolean => Future[Seq[Topic]] = _ => ???
) extends TopicService(null, null) {

  override def fetch(refresh: Boolean): Future[Seq[Topic]] = fetchMock(refresh)
}
