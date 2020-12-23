package io.codegalaxy.api

import io.codegalaxy.api.chapter._
import io.codegalaxy.api.question._
import io.codegalaxy.api.stats._
import io.codegalaxy.api.topic._
import io.codegalaxy.api.user._
import scommons.api.http.{ApiHttpClient, ApiHttpResponse, ApiHttpStatusException}
import scommons.api.http.ApiHttpClient.queryParams
import scommons.api.http.ApiHttpData.UrlEncodedFormData
import scommons.api.http.ApiHttpMethod._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CodeGalaxyApiClient(client: ApiHttpClient)
  extends UserApi
  with TopicApi
  with StatsApi
  with ChapterApi
  with QuestionApi {

  ////////////////////////////////////////////////////////////////////////////////////////
  // user api

  def authenticate(user: String, password: String): Future[Unit] = {
    client.exec(POST, "/auth/authenticate/userpass", Some(UrlEncodedFormData(Map(
      "username" -> List(user),
      "password" -> List(password)
    )))).map {
      case resp if resp.status != 200 =>
        throw ApiHttpStatusException("Login failed", ApiHttpResponse(resp.url, resp.status, Map.empty, ""))
      case _ => ()
    }
  }

  def logout(): Future[Unit] = {
    client.exec(GET, "/auth/logout", None).map {
      case resp if resp.status != 200 && resp.status != 303 =>
        throw ApiHttpStatusException("Logout failed", ApiHttpResponse(resp.url, resp.status, Map.empty, ""))
      case _ => ()
    }
  }

  def getUserProfile(force: Boolean): Future[Option[UserProfileData]] = {
    client.exec(GET, "/v1/profile", None, params = queryParams(
      "force" -> Some(force)
    )).map {
      case resp if resp.status == 401 => None
      case resp => Some(ApiHttpClient.parseResponse[UserProfileData](resp))
    }
  }

  def getUser: Future[UserData] = {
    client.execGet[UserData]("/v1/user")
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // topic api

  def getTopics: Future[List[TopicWithInfoData]] = {
    client.execGet[List[TopicWithInfoData]]("/v1/topics", params = queryParams(
      "info" -> Some(true)
    ))
  }

  def getTopicIcon(alias: String): Future[Option[String]] = {
    client.exec(GET, s"/app/assets/images/icons/light/icon__$alias.svg", None).map {
      case resp if resp.status == 200 => Some(resp.body)
      case _ => None
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // stats api

  def getStats: Future[List[StatsRespData]] = {
    client.execGet[List[StatsRespData]]("/v1/stats")
  }

  def getStatsTopic(topic: String): Future[StatsData] = {
    client.execGet[StatsData](s"/v1/stats/topic/$topic")
  }

  def getStatsChapter(topic: String, chapter: String): Future[StatsData] = {
    client.execGet[StatsData](s"/v1/stats/topic/$topic/modules/$chapter")
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////
  // chapters api

  def getChapters(topic: String): Future[List[ChapterRespData]] = {
    client.execGet[List[ChapterRespData]](s"/v1/topics/$topic/modules")
  }

  def getChaptersWithStatistics(topic: String): Future[List[ChapterWithStatisticsRespData]] = {
    client.execGet[List[ChapterWithStatisticsRespData]](s"/v1/topics/$topic/modulesWithStats")
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  // question api

  def getNextQuestion(topic: String, chapter: String): Future[QuestionData] = {
    client.execGet[QuestionData](s"/v1/topics/$topic/modules/$chapter/question")
  }

  def submitAnswer(topic: String, chapter: String, data: QuestionData): Future[QuestionData] = {
    client.execPost[QuestionData, QuestionData](s"/v1/topics/$topic/modules/$chapter/question", data)
  }
}
