package io.codegalaxy.app.user

import io.codegalaxy.api.user.{UserApi, UserData, UserProfileData}
import io.codegalaxy.app.user.UserService._
import io.codegalaxy.domain.ProfileEntity
import io.codegalaxy.domain.dao.ProfileDao
import scommons.reactnative.Image

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserService(api: UserApi, dao: ProfileDao) {

  def fetchProfile(refresh: Boolean = false): Future[Option[ProfileEntity]] = {
    for {
      existing <-
        if (refresh) Future.successful(None)
        else dao.getCurrent
      res <-
        if (existing.nonEmpty) Future.successful(existing)
        else {
          for {
            maybeProfile <- getProfileData.map { maybeData =>
              maybeData.map { case (profile, user) =>
                convertToProfileEntity(profile, user)
              }
            }
            _ <- removeProfile()
            _ <- maybeProfile match {
              case None => Future.successful(())
              case Some(profile) => dao.insert(profile)
            }
          } yield maybeProfile
        }
      _ <- res.flatMap(_.avatarUrl) match {
        case None => Future.successful(())
        case Some(avatarUrl) => Image.prefetch(avatarUrl)
      }
    } yield res
  }
  
  def removeProfile(): Future[Unit] = {
    dao.deleteAll().map(_ => ())
  }

  private def getProfileData: Future[Option[(UserProfileData, UserData)]] = {
    for {
      maybeProfile <- api.getUserProfile(force = true)
      maybeUser <-
        if (maybeProfile.isDefined) api.getUser.map(Some(_))
        else Future.successful(None)
    } yield {
      maybeProfile.flatMap { profile =>
        maybeUser.map { user =>
          (profile, user)
        }
      }
    }
  }
}

object UserService {

  private def convertToProfileEntity(profile: UserProfileData,
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
