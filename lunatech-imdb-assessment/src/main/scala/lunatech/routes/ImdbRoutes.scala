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
import lunatech.models.{InfoTitle, Informations, ErrorDescription, TopRatedMovies}
import lunatech.models.Title

//#import-json-formats
//#title-routes-class
class ImdbRoutes(imdbRegistry: ActorRef[ImdbRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#title-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import lunatech.serializer.JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getInfo(primaryTitle: String): Future[Either[ErrorDescription, Informations]] =
    imdbRegistry.ask(GetInfos(primaryTitle, _))
  def getMovies(genre: String): Future[Either[ErrorDescription, TopRatedMovies]] =
    imdbRegistry.ask(GetMovies(genre, _))

  //#all-routes
  val imdbRoutes: Route =
    pathPrefix("title") {
      concat(
        //#title-get-informations
        path(Segment) { primaryTitle =>
          get {
            rejectEmptyResponse {
              complete(getInfo(primaryTitle))
            }
          }
        },
        //#title-get-informations
        //#title-top-rated-movies
        pathPrefix("toprated"){
          path(Segment) { genre =>
            concat(
              get {
                rejectEmptyResponse {
                    complete(getMovies(genre))
                }
              }
            )
          }
        }
        //#title-top-rated-movies
      )
    }
  //#all-routes
}
