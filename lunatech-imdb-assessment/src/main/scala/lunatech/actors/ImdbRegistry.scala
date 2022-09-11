package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import lunatech.models.{Title, Titles, Error}
import lunatech.database.QueryDatabase
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContextExecutor

import scala.concurrent.ExecutionContext

object  ImdbRegistry {

  sealed trait Command
  final case class GetTitles(replyTo: ActorRef[Either[Error, Titles]]) extends Command
  final case class CreateTitle(title: Title, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetTitle(name: String, replyTo: ActorRef[GetTitleResponse]) extends Command

  final case class GetTitleResponse(maybeTitle: Option[Title])
  final case class ActionPerformed(description: String)

  val queryDatabase = new QueryDatabase
  
  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
    implicit val executionContext: ExecutionContext = context.executionContext
    registry(Set.empty)
    }
  }

  private def registry(titles: Set[Title])(implicit executionContext: ExecutionContext): Behavior[Command] =

    Behaviors.receiveMessage {
      case GetTitles(replyTo) =>
        val titlesQuery = queryDatabase.getTitle()
        
        titlesQuery.onComplete  {
          case Success(title) => {
            println(title)
            replyTo ! Right(Titles(title.toSeq))
          } 
          case Failure(exception) => {
            println(s"nik mok, an exception occured ${exception}") 
            replyTo ! Left(Error(s"an error occured ${exception}"))
          }
        }        
        Behaviors.same
      
      case CreateTitle(title, replyTo) =>
        replyTo ! ActionPerformed(s"Title ${title.primaryTitle} created.")
        registry(titles + title)
      
      case GetTitle(name, replyTo) =>
        replyTo ! GetTitleResponse(titles.find(_.primaryTitle == name))
        Behaviors.same
    }
}
