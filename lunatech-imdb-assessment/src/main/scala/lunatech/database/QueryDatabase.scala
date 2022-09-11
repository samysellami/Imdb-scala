package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import lunatech.database.DatabaseCommands
import lunatech.models.{InfoTitle, Cast, Title, Name}
import slick.jdbc.GetResult

class QueryDatabase {
  
    val databaseCommands = new DatabaseCommands
    
    def getInfo(primaryTitle: String) = {
        // implicit val getResultMovie =
        //     GetResult(r => InfoTitle(r.<<, r.<<, r.<<))

        // val sqlQuery =    sql"""SELECT titleType, primaryTitle, genres 
        //                         FROM public.title_basics 
        //                         WHERE primaryTitle=${primaryTitle} OR originalTitle=${primaryTitle};""".as[InfoTitle]

        implicit val getResultMovie =
            GetResult(
                r => InfoTitle(
                    Title(r.nextString(), r.nextString(), r.nextString(), r.nextInt(), r.nextInt(), r.nextInt(), r.nextString()), 
                    Cast (
                        Name(r.nextString(), r.nextInt(), r.nextInt(), r.nextString(), r.nextString()),
                        r.nextString(), r.nextString(), r.nextString()
                    )
                )
            )

        val sqlQuery =   sql"""SELECT   COALESCE(titleType,''),
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
                                        COALESCE(characters,'')
                                FROM public.title_basics t
                                INNER JOIN public.title_principals p
                                    ON t.tconst = p.tconst
                                INNER JOIN public.name_basics n
                                    ON p.nconst = n.nconst
                                WHERE primaryTitle=${primaryTitle} OR originalTitle=${primaryTitle};""".as[InfoTitle]

        databaseCommands.run[Vector[InfoTitle]](sqlQuery)        
    }
        
}
