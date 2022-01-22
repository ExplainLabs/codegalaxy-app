package io.codegalaxy.app.user

import io.codegalaxy.api.user._
import io.codegalaxy.app.BaseDBContextSpec
import io.codegalaxy.domain.ProfileEntity
import io.codegalaxy.domain.dao.ProfileDao

import scala.concurrent.Future

class UserServiceSpec extends BaseDBContextSpec {

  //noinspection TypeAnnotation
  class Api {
    val getUserProfile = mockFunction[Boolean, Future[Option[UserProfileData]]]
    val getUser = mockFunction[Future[UserData]]

    val api = new MockUserApi(
      getUserProfileMock = getUserProfile,
      getUserMock = getUser
    )
  }

  it should "fetch profile and save it in DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new ProfileDao(ctx)
    val service = new UserService(api.api, dao)
    val (profile, user) = getProfileData(123)

    api.getUserProfile.expects(*).onCall { force: Boolean =>
      force shouldBe true
      Future.successful(Some(profile))
    }
    api.getUser.expects().returning(Future.successful(user))

    val beforeF = service.removeProfile()
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.fetchProfile()
    }

    //then
    for {
      res <- resultF
      curr <- dao.getCurrent
    } yield {
      res shouldBe curr
      res shouldBe Some(toProfileEntity(profile, user))
    }
  }
  
  it should "remove profile from DB if not authenticated" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new ProfileDao(ctx)
    val service = new UserService(api.api, dao)

    api.getUserProfile.expects(*).onCall { force: Boolean =>
      force shouldBe true
      Future.successful(None)
    }
    api.getUser.expects().never()

    val (profile, user) = getProfileData(123)
    val beforeF = for {
      _ <- service.removeProfile()
      _ <- dao.insert(toProfileEntity(profile, user))
      Some(existing) <- dao.getCurrent
    } yield existing
    
    //when
    val resultF = beforeF.flatMap { _ =>
      service.fetchProfile(refresh = true)
    }

    //then
    for {
      res <- resultF
      curr <- dao.getCurrent
    } yield {
      res shouldBe curr
      res shouldBe None
    }
  }
  
  it should "refresh profile in DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new ProfileDao(ctx)
    val service = new UserService(api.api, dao)
    val (profile, user) = getProfileData(123)
    val (newProfile, newUser) = getProfileData(456)

    api.getUserProfile.expects(*).onCall { force: Boolean =>
      force shouldBe true
      Future.successful(Some(newProfile))
    }
    api.getUser.expects().returning(Future.successful(newUser))

    val beforeF = for {
      _ <- service.removeProfile()
      _ <- dao.insert(toProfileEntity(profile, user))
      Some(existing) <- dao.getCurrent
    } yield existing

    //when
    val resultF = for {
      existing <- beforeF
      res <- service.fetchProfile(refresh = true)
    } yield (existing, res)

    //then
    for {
      (existing, res) <- resultF
      curr <- dao.getCurrent
    } yield {
      existing shouldBe toProfileEntity(profile, user)
      res shouldBe curr
      res shouldBe Some(toProfileEntity(newProfile, newUser))
    }
  }

  it should "return local data from DB" in withCtx { ctx =>
    //given
    val api = new Api
    val dao = new ProfileDao(ctx)
    val service = new UserService(api.api, dao)
    val (profile, user) = getProfileData(123)

    api.getUserProfile.expects(*).never()
    api.getUser.expects().never()

    val beforeF = for {
      _ <- service.removeProfile()
      _ <- dao.insert(toProfileEntity(profile, user))
    } yield ()

    //when
    val resultF = for {
      _ <- beforeF
      res <- service.fetchProfile()
    } yield res

    //then
    for {
      res <- resultF
      curr <- dao.getCurrent
    } yield {
      res shouldBe curr
      res shouldBe Some(toProfileEntity(profile, user))
    }
  }
  
  private def getProfileData(userId: Int): (UserProfileData, UserData) = {
    val username = s"username$userId"
    (UserProfileData(
      userId = userId,
      username = username,
      city = Some("test city"),
      firstName = Some("test firstName"),
      lastName = Some("test lastName")
    ), UserData(
      username = username,
      email = Some("test email"),
      fullName = Some("Test FullName"),
      avatarUrl = Some("/test/avatar/url")
    ))
  }

  private def toProfileEntity(profile: UserProfileData,
                              user: UserData): ProfileEntity = {
    ProfileEntity(
      id = profile.userId,
      username = profile.username,
      email = user.email,
      firstName = profile.firstName,
      lastName = profile.lastName,
      fullName = user.fullName,
      city = profile.city,
      avatarUrl = user.avatarUrl
    )
  }
}
