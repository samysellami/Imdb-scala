package lunatech.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import lunatech.actors.ImdbRegistry
import lunatech.actors.ImdbRegistry._
import lunatech.models.{Title, Titles, ErrorDescription}

//#import-json-formats
//#title-routes-class
class ImdbRoutes(imdbRegistry: ActorRef[ImdbRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#title-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import lunatech.serializer.JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getTitles(): Future[Either[ErrorDescription, Titles]] =
    imdbRegistry.ask(GetTitles)
  def getTitle(name: String): Future[GetTitleResponse] =
    imdbRegistry.ask(GetTitle(name, _))

  //#all-routes
  //#titles-get title-get
  val imdbRoutes: Route =
    pathPrefix("titles") {
      concat(
        //#titles-get
        pathEnd {
          concat(
            get {
              complete(getTitles())
            })
        },
        //#titles-get
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-title-info
              rejectEmptyResponse {
                onSuccess(getTitle(name)) { response =>
                  complete(response.maybeTitle)
                }
              }
              //#retrieve-title-info
            })
        })
    }
  //#all-routes
}
