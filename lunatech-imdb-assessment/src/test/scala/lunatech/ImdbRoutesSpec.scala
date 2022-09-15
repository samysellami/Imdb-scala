package lunatech

//#user-routes-spec
//#test-top
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// import lunatech.actors.ImdbRegistryActor
// import lunatech.actors.ImdbRegistryActor._
import lunatech.routes.ImdbRoutes
import lunatech.models.{InfoTitle, Crew, Principals, Title}


class ImdbRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  //#test-top

  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  // val userRegistry = testKit.spawn(ImdbRegistryActor())
  // lazy val routes = new ImdbRoutes(userRegistry).imdbRoutes

  // use the json formats to marshal and unmarshall objects in the test
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import lunatech.serializer.JsonFormats._

  // #actual-test
  "ImdbRoutes" should {
    "return no users if no present (GET /title)" in {

      // val request = HttpRequest(uri = "/title")

      // request ~> routes ~> check {
      //   status should ===(StatusCodes.OK)

      //   // we expect the response to be json:
      //   contentType should ===(ContentTypes.`application/json`)

      //   // and no entries should be in the list:
      //   entityAs[String] should ===("""{"users":[]}""")
      // }
    }
  }
  // #actual-test

}
