package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import lunatech.models.{Title, Titles}
import lunatech.database.QueryDatabase
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.model.headers.LinkParams

import scala.concurrent.ExecutionContext

object  ImdbRegistry {
  // actor protocol

  sealed trait Command
  final case class GetTitles(replyTo: ActorRef[Titles]) extends Command
  final case class CreateTitle(title: Title, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetTitle(name: String, replyTo: ActorRef[GetTitleResponse]) extends Command
  final case class DeleteTitle(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

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
        val title_basics = queryDatabase.getTitle()
        
        title_basics.onComplete  {
          case Success(title) => {
            println(title)
            replyTo ! Titles(title.toSeq)} 
          case Failure(exception) => {
            println(s"nik mok, an exception occured ${exception}") 
            replyTo ! Titles(titles.toSeq)
          }
        }
        
        Behaviors.same
      case CreateTitle(title, replyTo) =>
        replyTo ! ActionPerformed(s"Title ${title.primaryTitle} created.")
        registry(titles + title)
      case GetTitle(name, replyTo) =>
        replyTo ! GetTitleResponse(titles.find(_.primaryTitle == name))
        Behaviors.same
      
      case DeleteTitle(name, replyTo) =>
        replyTo ! ActionPerformed(s"Title $name deleted.")
        registry(titles.filterNot(_.primaryTitle == name))
    }
}
//#title-registry-actor
