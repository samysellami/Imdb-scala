package lunatech.actors

import scala.concurrent.ExecutionContext
import akka.actor.typed.ActorRef
import scala.util.{Success, Failure}

import lunatech.database.QueryDatabase
import lunatech.models.{InfoTitle, Informations, ErrorDescription, TopRatedMovies, Principals, Crew}

class DatabaseConnector(implicit executionContext: ExecutionContext) {

  val queryDatabase = new QueryDatabase

  def performInfosQuery(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, Informations]]) = {
    val queryResult = queryDatabase.getInfoQuery(primaryTitle)
    queryResult.onComplete  {
      case Success(infos) => {
        val informations = Utils.aggregateData(infos.toSeq)
        replyTo ! Right(Informations(informations))
      } 
      case Failure(exception) => {
        println(s"An exception occured: ${exception}") 
        replyTo ! Left(ErrorDescription(s"an error occured ${exception}"))
      }
    }        
  }

  def performMoviesQuery(genre: String, replyTo: ActorRef[Either[ErrorDescription, TopRatedMovies]]) = {
    val queryResult = queryDatabase.getMoviesQuery(genre)
    queryResult.onComplete  {
      case Success(movies) => {
        replyTo ! Right(TopRatedMovies(movies.toSeq))
      } 
      case Failure(exception) => {
        println(s"An exception occured: ${exception}") 
        replyTo ! Left(ErrorDescription(s"an error occured ${exception}"))
      }
    }        
  }

  def performSeparationQuery(actor: String, replyTo: ActorRef[Either[ErrorDescription, String]]) = {
    val queryResult = queryDatabase.sixDegreeQuery(actor)
    queryResult.onComplete  {
      case Success(separation) => {
        println("cast names:", separation)
        replyTo ! Right(separation.headOption.getOrElse(""))
      } 
      case Failure(exception) => {
        println(s"An exception occured: ${exception}") 
        replyTo ! Left(ErrorDescription(s"an error occured ${exception}"))
      }
    }        
  }

}
