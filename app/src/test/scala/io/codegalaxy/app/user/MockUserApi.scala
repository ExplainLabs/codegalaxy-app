package io.codegalaxy.app.user

import io.codegalaxy.api.user.{UserApi, UserData, UserProfileData}

import scala.concurrent.Future

//noinspection NotImplementedCode
class MockUserApi(
   authenticateMock: (String, String) => Future[Unit] = (_, _) => ???,
   logoutMock: () => Future[Unit] = () => ???,
   getUserProfileMock: Boolean => Future[Option[UserProfileData]] = _ => ???,
   getUserMock: () => Future[UserData] = () => ???
) extends UserApi {

  override def authenticate(user: String, password: String): Future[Unit] =
    authenticateMock(user, password)

  override def logout(): Future[Unit] = logoutMock()

  override def getUserProfile(force: Boolean): Future[Option[UserProfileData]] =
    getUserProfileMock(force)

  override def getUser: Future[UserData] = getUserMock()
}
