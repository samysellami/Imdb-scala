package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.duration._

import lunatech.database.DatabaseCommands
import lunatech.models.{InfoTitle, Principals, Crew, Title, Name, Rating, RatedMovie, Actor}

class QueryDatabase(implicit executionContext: ExecutionContext) {

  val databaseCommands = new DatabaseCommands

  def getInfoQuery(primaryTitle: String) = {
    implicit val getResultMovie =
      GetResult(r =>
        InfoTitle(
          Title(
            r.nextString(), r.nextString(),r.nextString(),r.nextInt(),r.nextInt(),r.nextInt(),r.nextString()
          ),
          List(
            Principals(
              Name(
                r.nextString(),r.nextInt(),r.nextInt(),r.nextString(),r.nextString()
              ),
              r.nextString(),r.nextString(),r.nextString()
            )),
          List(Crew(r.nextString(), r.nextString()))
        )
      )
    val sqlQuery = 
      sql"""SELECT   
              COALESCE(titleType,''),
              COALESCE(primaryTitle,''),
              COALESCE(originalTitle,''),
              COALESCE(startYear, '0'),
              COALESCE(endYear, '0'), 
              COALESCE(runtimeMinutes, '0'),
              COALESCE(genres,''),          
              COALESCE(primaryName,''),
              COALESCE(birthYear,0),
              COALESCE(deathYear,0),
              COALESCE(primaryProfession,''),
              COALESCE(knownForTitles,''),
              COALESCE(category,''),
              COALESCE(job,''),
              COALESCE(characters,''),
              COALESCE(directors,''),
              COALESCE(writers,'')
            FROM public.title_basics t
              INNER JOIN public.title_principals p
                ON t.tconst = p.tconst
              INNER JOIN public.name_basics n
                ON p.nconst = n.nconst
              INNER JOIN public.title_crew c
                ON t.tconst = c.tconst
            WHERE primaryTitle=${primaryTitle} OR originalTitle=${primaryTitle};"""
      .as[InfoTitle]

    databaseCommands.run[Vector[InfoTitle]](sqlQuery)
  }

  def getMoviesQuery(genre: String) = {
    implicit val getResultMovie =
      GetResult(r =>
        RatedMovie(
          r.nextString(),
          Rating(r.nextFloat(), r.nextInt())
        )
      )
    val sqlQuery = 
      sql"""SELECT 
              COALESCE(primaryTitle,''),
              COALESCE(averageRating,0),
              COALESCE(numVotes,0)
            FROM public.title_basics t
              INNER JOIN public.title_ratings r
                ON t.tconst = r.tconst
            WHERE genres=${genre}
            ORDER BY averageRating DESC, numVotes DESC 
              OFFSET 0 ROWS 
              FETCH FIRST 10 ROWS ONLY;""".as[RatedMovie]

    databaseCommands.run[Vector[RatedMovie]](sqlQuery)
  }

  def getCastNames(movie: String): Seq[String] = {
    val sqlQuery = 
      sql"""SELECT COALESCE(nconst,'')
              FROM public.title_basics t
                INNER JOIN public.title_principals p
                  ON t.tconst = p.tconst
              WHERE t.tconst=${movie};"""
    val cast = databaseCommands.runQuery(sqlQuery)
    Await.result(cast, 2.seconds)
  }

  def sixDegreeQuery(actorName: String) = {

    val sqlQuery = 
      sql"""WITH 
              nconstActor AS (
                SELECT nconst 
                FROM public.name_basics n
                WHERE primaryName=${actorName}
              )
            SELECT t.tconst 
              FROM public.title_basics t
                INNER JOIN public.title_principals p
                  ON t.tconst = p.tconst
            WHERE nconst=(SELECT * FROM nconstActor);"""
    val moviesActor = databaseCommands.runQuery(sqlQuery)
    
    var castActor: Seq[String] = Seq.empty[String]
    moviesActor.map(
      movies => {
        movies.map( 
          movie => {
            val cast = getCastNames(movie)  
            castActor = castActor ++ cast.toSeq
          }
        )
        castActor.distinct
      }
    )
  }
}
