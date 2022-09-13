package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import lunatech.models.{InfoTitle, Informations, ErrorDescription, TopRatedMovies, Principals, Crew}
import lunatech.database.QueryDatabase
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContextExecutor

import scala.concurrent.ExecutionContext
import lunatech.models.Title

object  ImdbRegistry {

  sealed trait Command
  final case class GetInfos(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, Informations]]) extends Command
  final case class GetMovies(genre: String, replyTo: ActorRef[Either[ErrorDescription, TopRatedMovies]]) extends Command

  val queryDatabase = new QueryDatabase
  
  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
      implicit val executionContext: ExecutionContext = context.executionContext
      registry()
    }
  }

  def performInfosQuery(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, Informations]])(implicit executionContext: ExecutionContext) = {
    val queryResult = queryDatabase.getInfo(primaryTitle)
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

  def performMoviesQuery(genre: String, replyTo: ActorRef[Either[ErrorDescription, TopRatedMovies]])(implicit executionContext: ExecutionContext) = {
    val queryResult = queryDatabase.getMovies(genre)
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

  private def registry()(implicit executionContext: ExecutionContext): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetInfos(primaryTitle, replyTo) =>
        performInfosQuery(primaryTitle, replyTo)
        Behaviors.same
      case GetMovies(genre, replyTo) =>
        performMoviesQuery(genre, replyTo)
        Behaviors.same
    }
}
