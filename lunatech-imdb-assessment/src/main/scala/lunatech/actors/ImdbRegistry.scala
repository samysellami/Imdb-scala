package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import lunatech.models.{Title, Titles, ErrorDescription}
import lunatech.database.QueryDatabase
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContextExecutor

import scala.concurrent.ExecutionContext

object  ImdbRegistry {

  sealed trait Command
  final case class GetTitles(replyTo: ActorRef[Either[ErrorDescription, Titles]]) extends Command
  final case class GetTitle(name: String, replyTo: ActorRef[GetTitleResponse]) extends Command
  final case class GetTitleResponse(maybeTitle: Option[Title])

  val queryDatabase = new QueryDatabase
  
  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
    implicit val executionContext: ExecutionContext = context.executionContext
    registry(Set.empty)
    }
  }

  def performQuery(replyTo: ActorRef[Either[ErrorDescription, Titles]])(implicit executionContext: ExecutionContext) = {
    val titlesQuery = queryDatabase.getTitle()
    
    titlesQuery.onComplete  {
      case Success(titles) => {
        println(titles)
        replyTo ! Right(Titles(titles.toSeq))
      } 
      case Failure(exception) => {
        println(s"nik mok, an exception occured ${exception}") 
        replyTo ! Left(ErrorDescription(s"an error occured ${exception}"))
      }
    }        
  }

  private def registry(titles: Set[Title])(implicit executionContext: ExecutionContext): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetTitles(replyTo) =>
        performQuery(replyTo)
        Behaviors.same
      case GetTitle(name, replyTo) =>
        replyTo ! GetTitleResponse(titles.find(_.primaryTitle == name))
        Behaviors.same
    }
}
