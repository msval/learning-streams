import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths
import java.security.Timestamp

object ReactiveTweets extends App {

  final case class Author(handle: String)
  final case class HashTag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[HashTag] =
      body.split(" ").collect {case t if t.startsWith("#") => HashTag(t) }.toSet
  }

  val akkaTag = HashTag("#akka")

  implicit val system = ActorSystem("reactive-tweets")

  implicit val materializer = ActorMaterializer()

  val tweets: Source[Tweet, NotUsed] = Source(Nil)

  val authors: Source[Author, NotUsed] =
    tweets.filter(_.hashtags.contains(akkaTag))
    .map(_.author)

  Sink

}
