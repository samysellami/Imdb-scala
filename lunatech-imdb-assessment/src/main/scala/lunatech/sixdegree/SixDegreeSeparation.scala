package lunatech.sixdegree

import lunatech.database.QueryDatabase
import scala.concurrent.ExecutionContext
import scala.concurrent._

import lunatech.models.{InfoTitle, Principals, Crew, Title, Name, Rating, RatedMovie, Actor}
import scala.collection.mutable.Queue
import scala.util.control.Breaks._
import lunatech.models.ErrorDescription

class SixDegreeSeparation(implicit executionContext: ExecutionContext) {
  
  val queryDatabase = new QueryDatabase
  
  def getCastActor(titlesActor: Seq[String]): Seq[String] = {
    var castActor: Seq[String] = Seq.empty[String]
    val cast = titlesActor.map(
      title => {
        val cast = queryDatabase.getCastNames(title)  
        castActor = castActor ++ cast
      }
    )
    castActor.toSeq.distinct
    // Await.ready(cast, Duration.Inf)
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
      val titlesActor = queryDatabase.getTitlesActor(actor.nconst)
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
    val titlesActor = queryDatabase.getTitlesActor(actor.nconst)
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

  def shortestPath(nconstActor: String, nconstKevinBacon: String): Actor = {
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
    endActor
  }

  def sixDegree(actorName: String): Future[String] = {
    val nconstKevinBacon = queryDatabase.getNconstKevinBacon().headOption.getOrElse("")
    val nconstActor = queryDatabase.getNconstActor(actorName).headOption.getOrElse("")
    println(s"${actorName}= ${nconstActor}")
    println(s"kevin bacon = ${nconstKevinBacon}")
    // val graph = buildGraph(startActor, nconstKevinBacon)

    val result = shortestPath(nconstActor, nconstKevinBacon)
    val route = createRoute(List(result), Some(result))
    println("***shortest path:  ****")
    route.foreach(actor => println(actor.nconst))
    
    Future.successful("6")
  }

}
