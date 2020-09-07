package io.codegalaxy.app.topic

case class TopicParams(topic: String,
                       chapter: Option[String] = None) {

  def getChapter: String = chapter.getOrElse {
    throw new IllegalArgumentException(s"Chapter is not specified, params: $this")
  }

  def toMap: Map[String, String] = {
    Seq(
      "topic" -> Some(topic),
      "chapter" -> chapter
    ).collect {
        case (k, Some(v)) => (k, s"$v")
      }.toMap
  }
}

object TopicParams {

  def fromMap(params: Map[String, String]): TopicParams = {
    TopicParams(
      topic = params.getOrElse("topic", ""),
      chapter = params.get("chapter")
    )
  }
}
