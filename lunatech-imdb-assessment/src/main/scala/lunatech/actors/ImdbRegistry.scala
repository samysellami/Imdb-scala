package lunatech.actors

//#title-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable


object  ImdbRegistry {
  // actor protocol
  sealed trait Command
  final case class GetTitles(replyTo: ActorRef[Titles]) extends Command
  final case class CreateTitle(title: Title, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetTitle(name: String, replyTo: ActorRef[GetTitleResponse]) extends Command
  final case class DeleteTitle(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetTitleResponse(maybeTitle: Option[Title])
  final case class ActionPerformed(description: String)

  //#title-case-classes
  final case class Title(name: String, titleType: String, genres: String)
  final case class Titles(titles: immutable.Seq[Title])
  //#title-case-classes

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(titles: Set[Title]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetTitles(replyTo) =>
        replyTo ! Titles(titles.toSeq)
        Behaviors.same
      case CreateTitle(title, replyTo) =>
        replyTo ! ActionPerformed(s"Title ${title.name} created.")
        registry(titles + title)
      case GetTitle(name, replyTo) =>
        replyTo ! GetTitleResponse(titles.find(_.name == name))
        Behaviors.same
      case DeleteTitle(name, replyTo) =>
        replyTo ! ActionPerformed(s"Title $name deleted.")
        registry(titles.filterNot(_.name == name))
    }
}
//#title-registry-actor
