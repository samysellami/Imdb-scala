package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import lunatech.models.{InfoTitle, InfosTitle, ErrorDescription}
import lunatech.database.QueryDatabase
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContextExecutor

import scala.concurrent.ExecutionContext

object  ImdbRegistry {

  sealed trait Command
  final case class GetInfo(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, InfosTitle]]) extends Command
  final case class GetMovies(genre: String, replyTo: ActorRef[Either[ErrorDescription, InfosTitle]]) extends Command

  val queryDatabase = new QueryDatabase
  
  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
      implicit val executionContext: ExecutionContext = context.executionContext
      registry()
    }
  }

  def performQuery(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, InfosTitle]])(implicit executionContext: ExecutionContext) = {
    val queryResult = queryDatabase.getInfo(primaryTitle)
    queryResult.onComplete  {
      case Success(infos) => {
        println(infos)
        replyTo ! Right(InfosTitle(infos.toSeq))
      } 
      case Failure(exception) => {
        println(s"nik mok, an exception occured ${exception}") 
        replyTo ! Left(ErrorDescription(s"an error occured ${exception}"))
      }
    }        
  }

  private def registry()(implicit executionContext: ExecutionContext): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetInfo(primaryTitle, replyTo) =>
        performQuery(primaryTitle, replyTo)
        Behaviors.same
      case GetMovies(genre, replyTo) =>
        performQuery(genre, replyTo)
        Behaviors.same
    }
}
