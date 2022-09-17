package lunatech.sixdegree

import lunatech.database.QueryDatabase
import scala.concurrent.ExecutionContext
import scala.concurrent._

import lunatech.models.{InfoTitle, Principals, Crew, Title, Name, Rating, RatedMovie, Actor}

class SixDegreeSeparation(implicit executionContext: ExecutionContext) {
  
  val queryDatabase = new QueryDatabase
  def getCastActor(titlesActor: Seq[String]): Seq[String] = {
    var castActor: Seq[String] = Seq.empty[String]
    // var castActor = new ListBuffer[String]()
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

  def buildGraph(actor: Actor, nconstKevinBacon: String, i: Int): Actor = {
    if (i<3 && actor.nconst != nconstKevinBacon && actor.nconst != null){
      val titlesActor = queryDatabase.getTitlesActor(actor.nconst)
      val castActor = getCastActor(titlesActor)
      val actors: List[Actor] = createCastActor(castActor)
      
      val actorsCast = actors.map(
        actor => buildGraph(actor, nconstKevinBacon, i+1)
      )
      actor.copy(castActors = actorsCast)
    } else {
      actor
    }
  }

  def sixDegree(actorName: String): Future[String] = {
    val nconstKevinBacon = queryDatabase.getNconstKevinBacon().headOption.getOrElse("")
    val nconstActor = queryDatabase.getNconstActor(actorName).headOption.getOrElse("")
    val actor = Actor(nconstActor)
    val i = 1
    val graph = buildGraph(actor, nconstKevinBacon, i)
    println(graph)

    Future.successful("6")
  }

}
