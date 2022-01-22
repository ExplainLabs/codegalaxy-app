package io.codegalaxy.app.user

import io.codegalaxy.domain.ProfileEntity

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockUserService(
  fetchProfileMock: Boolean => Future[Option[ProfileEntity]] = _ => ???,
  removeProfileMock: () => Future[Unit] = () => ???
) extends UserService(null, null) {

  override def fetchProfile(refresh: Boolean): Future[Option[ProfileEntity]] =
    fetchProfileMock(refresh)
    
  override def removeProfile(): Future[Unit] =
    removeProfileMock()
}
