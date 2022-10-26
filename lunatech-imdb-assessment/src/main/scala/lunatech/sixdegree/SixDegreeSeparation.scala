package lunatech.sixdegree

import lunatech.database.QueryDatabase
import scala.concurrent.ExecutionContext
import scala.concurrent._

import lunatech.models.{InfoTitle, Principals, Crew, Title, Name, Rating, RatedMovie, Actor}
import scala.collection.mutable.Queue
import scala.util.control.Breaks._
import lunatech.models.ErrorDescription

/**
 * A class that computes the degrees of separation between a person and Kevin Bacon 
 * 
 */
class SixDegreeSeparation(implicit executionContext: ExecutionContext) {
  
  val queryDatabase = new QueryDatabase
  
  def getCastActor(titlesActor: Seq[String]): Seq[String] = {
    var castActor: Seq[String] = Seq.empty[String]
    val cast = titlesActor.map(
      title => {
        val cast = queryDatabase.getCastNamesQuery(title)  
        castActor = castActor ++ cast
      }
    )
    castActor.toSeq.distinct
  }
  
  def createCastActor(castActor: Seq[String]): List[Actor] = {
    val cast: List[Actor] = castActor.map (
      castName => 
        Actor(castName)
    ).toList
    cast
  }

  def buildGraph(actor: Actor, nconstKevinBacon: String): Actor = {
    if (actor.nconst != nconstKevinBacon && actor.nconst != null){
      val titlesActor = queryDatabase.getTitlesActorQuery(actor.nconst)
      val castActor = getCastActor(titlesActor)
      val actors: List[Actor] = createCastActor(castActor)
      val actorsCast = actors.map(
        actor => buildGraph(actor, nconstKevinBacon)
      )
      actor.copy(castActors = actorsCast)
    } else {
      actor
    }
  }

  def addCast(actor: Actor) = {
    val titlesActor = queryDatabase.getTitlesActorQuery(actor.nconst)
    val castActor = getCastActor(titlesActor)
    val actors: List[Actor] = createCastActor(castActor)
    actor.copy(castActors = actors)
  }

  def addElementList[T](list: List[T], element: T) = {
    list :+ element  
  }

  def createRoute(route: List[Actor], nodeActor: Option[Actor]): List[Actor] = {
    if (nodeActor.map(_.prevActor.isDefined).get){
      createRoute(addElementList[Actor](route, nodeActor.flatMap(_.prevActor).get), nodeActor.flatMap(_.prevActor))
    } else {
      route
    }
  }

  /** 
  *  Returns the shortest path between the actor argument and Kevin Bacon
  *  the method uses the Breath First Search algorithm
  * 
  *  @param actorName   the person's name
  *  @return either the degree of separation or and error message indicating that no path was found
  */
  def shortestPath(nconstActor: String, nconstKevinBacon: String): Either[String, Actor] = {
    var endActor = Actor("")
    var BFS = Queue[Actor]()
    val startActor = Actor(nconstActor, visited = true) 
    BFS.enqueue(startActor)
    if (nconstActor!=nconstKevinBacon){          
      breakable { while (!BFS.isEmpty) {
        val actor = addCast(BFS.dequeue)
        actor.castActors.foreach(
          actorNode => {
            if (!actorNode.visited){
              actorNode.visited = true
              actorNode.prevActor = Some(actor)
              BFS.enqueue(actorNode) 
              if (actorNode.nconst == nconstKevinBacon){
                  endActor = actorNode
                  break
              }
            }
          } 
        )
      }}
    }
    if (endActor.prevActor.isDefined){
      Right(endActor)
    } else {
      if (nconstActor==nconstKevinBacon)
        Left("Kevin Bacon has no degree of separation with himself!!")
      else
        Left("no path found!!")
    }
  }

  /** 
  *  Returns the degree of separation between the actor argument and Kevin Bacon
  *
  *  @param actorName   the person's name
  *  @return either the degree of separation or and error message
  */
  def sixDegree(actorName: String): Either[ErrorDescription, String] = {
    val nconstKevinBacon = queryDatabase.getNconstKevinBaconQuery().headOption.getOrElse("")
    val nconstActor = queryDatabase.getNconstActorQuery(actorName).headOption.getOrElse("")
    val result = shortestPath(nconstActor, nconstKevinBacon)
    
    result match {
      case Right(value) =>  {
        val route = createRoute(List(value), Some(value)).reverse
        val routeNames = route.map(
          actor => {
            queryDatabase.getNameActorQuery(actor.nconst).headOption.getOrElse("")
          }
        )
        val sixDegree = routeNames.foldLeft(routeNames(0))(
          (acc, name) => {
            if (name!=routeNames(0)) acc + " -> " + name
            else acc
          }
        )
        Right(s"${route.length -1} (${sixDegree})")
      }
      case Left(value) => {
        Left(ErrorDescription(value))
      }
    }    
  }
}
