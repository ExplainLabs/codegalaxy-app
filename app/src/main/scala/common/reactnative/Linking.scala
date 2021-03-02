package common.reactnative

import scala.concurrent.Future

trait Linking {

  protected def native: raw.Linking

  def openURL(url: String): Future[Unit] =
    native.openURL(url).toFuture
}

object Linking extends Linking {

  protected val native = raw.Linking
}
