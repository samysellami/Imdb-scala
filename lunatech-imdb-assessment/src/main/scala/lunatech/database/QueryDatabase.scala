package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import lunatech.database.DatabaseCommands
import lunatech.models.{
  InfoTitle,
  Principals,
  Crew,
  Title,
  Name,
  Rating,
  RatedMovie
}
import slick.jdbc.GetResult
import lunatech.models.RatedMovie

class QueryDatabase {

  val databaseCommands = new DatabaseCommands

  def getInfo(primaryTitle: String) = {
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

  def getMovies(genre: String) = {
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

}
