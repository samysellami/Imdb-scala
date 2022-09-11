package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import lunatech.database.DatabaseCommands
import lunatech.models.{InfoTitle, CastCrew}
import slick.jdbc.GetResult

class QueryDatabase {
  
    val databaseCommands = new DatabaseCommands
    
    def getInfo(primaryTitle: String) = {
        implicit val getResultMovie =
            GetResult(r => InfoTitle(r.<<, r.<<, r.<<))

        val sqlQuery =    sql"""SELECT titleType, primaryTitle, genres 
                                FROM public.title_basics 
                                WHERE primaryTitle=${primaryTitle} OR originalTitle=${primaryTitle};""".as[InfoTitle]
        val query =   sql"""SELECT *
                            FROM public.title_basics t
                            INNER JOIN public.title_principals p
                            ON t.tconst = p.tconst
                            AND t.primaryTitle='Pauvre Pierrot';"""
        databaseCommands.run[Vector[InfoTitle]](sqlQuery)        
    }
        
}
