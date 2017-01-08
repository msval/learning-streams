import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths
import java.security.Timestamp

object Hello extends App {

  //implicit val system = ActorSystem("QuickStart")
  //implicit val materializer = ActorMaterializer()

/*  val source: Source[Int, NotUsed] = Source(1 to 100)

  source.runForeach(println)

  var factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] =
    factorials.map(num => ByteString(s"$num\n"))
    .runWith(FileIO.toPath((Paths.get("factorials.txt"))))

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
    .map(s => ByteString(s + "\n"))
    .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  factorials.map(_.toString).runWith((lineSink("factorial2.txt")))

  val done: Future[Done] =
    factorials
    .zipWith(Source(0 to 100)) ((num, idx) => s"$idx != $num")
    .throttle(1, 1.second, 1, ThrottleMode.Shaping)
    .runForeach(println)*/

  final case class Author(handle: String)
  final case class HashTag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[HashTag] =
      body.split(" ").collect {case t if t.startsWith("#") => HashTag(t) }.toSet
  }

  val akkaTag = HashTag("#akka")

  implicit val system = ActorSystem("rective-tweets")
  implicit val materializer = ActorMaterializer()

  val tweets: Source[Tweet, NotUsed] = Source.empty

  val authors: Source[Author, NotUsed] =
    tweets.filter(_.hashtags.contains(akkaTag)).map(_.author)

  authors.runWith(Sink.foreach(println))

  val hashtags: Source[HashTag, NotUsed] = tweets.mapConcat(_.hashtags.toList)


}
