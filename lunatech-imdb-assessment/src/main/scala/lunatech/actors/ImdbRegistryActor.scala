package lunatech.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import scala.concurrent.ExecutionContext

import lunatech.models.{InfoTitle, Informations, ErrorDescription, TopRatedMovies, Principals, Crew}

object  ImdbRegistryActor {

  sealed trait Command
  final case class GetInfos(primaryTitle: String, replyTo: ActorRef[Either[ErrorDescription, Informations]]) extends Command
  final case class GetMovies(genre: String, replyTo: ActorRef[Either[ErrorDescription, TopRatedMovies]]) extends Command
  final case class GetSeparation(actor: String, replyTo: ActorRef[Either[ErrorDescription, String]]) extends Command

  def apply(): Behavior[Command] = {
    Behaviors.setup { context =>
      implicit val executionContext: ExecutionContext = context.executionContext
      val databaseConnector = new DatabaseConnector
      registry(databaseConnector)
    }
  }

  private def registry(databaseConnector: DatabaseConnector)(implicit executionContext: ExecutionContext): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetInfos(primaryTitle, replyTo) =>
        databaseConnector.performInfosQuery(primaryTitle, replyTo)
        Behaviors.same
      case GetMovies(genre, replyTo) =>
        databaseConnector.performMoviesQuery(genre, replyTo)
        Behaviors.same
      case GetSeparation(actor, replyTo) =>
        databaseConnector.performSeparationQuery(actor, replyTo)
        Behaviors.same
    }
}
