package io.codegalaxy.app

import scommons.reactnative.Image

import scala.concurrent.Future
import scala.scalajs.js

//noinspection NotImplementedCode
class MockImage(
  prefetchMock: String => Future[js.Any] = _ => ???
) extends Image {

  override def prefetch(url: String): Future[js.Any] = prefetchMock(url)
}
