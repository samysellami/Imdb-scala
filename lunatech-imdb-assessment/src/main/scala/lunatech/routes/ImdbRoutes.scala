package lunatech.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import lunatech.actors.ImdbRegistryActor
import lunatech.actors.ImdbRegistryActor._
import lunatech.models.{InfoTitle, Informations, ErrorDescription, TopRatedMovies}
import lunatech.models.Title

/**
 * A class that contains all the http routes that the application hadndles 
 *
 */
 class ImdbRoutes(imdbRegistry: ActorRef[ImdbRegistryActor.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats
  import lunatech.serializer.JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getInfos(primaryTitle: String): Future[Either[ErrorDescription, Informations]] =
    imdbRegistry.ask(GetInfos(primaryTitle, _))
  def getMovies(genre: String): Future[Either[ErrorDescription, TopRatedMovies]] =
    imdbRegistry.ask(GetMovies(genre, _))
  def getSeparation(actor: String): Future[Either[ErrorDescription, String]] = 
    imdbRegistry.ask(GetSeparation(actor, _))


  //#all-routes
  val imdbRoutes: Route =
    pathPrefix("title") {
      concat(
        //#title-get-informations
        path(Segment) { primaryTitle =>
          get {
            rejectEmptyResponse {
              complete(getInfos(primaryTitle))
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
        },
        //#title-top-rated-movies
        //#title-six-degrees-separation
        pathPrefix("sixdegrees"){
          path(Segment) { actor =>
            concat(
              get {
                rejectEmptyResponse {
                    complete(getSeparation(actor))
                }
              }
            )
          }
        }
        //#title-six-degrees-separation
      )
    }
  //#all-routes
}
