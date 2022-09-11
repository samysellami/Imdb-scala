package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import lunatech.database.DatabaseCommands
import lunatech.models.Title
import slick.jdbc.GetResult

class QueryDatabase {
  
    val databaseCommands = new DatabaseCommands
    
    def getTitle() = {
        implicit val getResultMovie =
            GetResult(r => Title(r.<<, r.<<, r.<<)) 
        
        val sqlQuery = sql"SELECT titleType, primaryTitle, genres FROM public.title_basics where primaryTitle='Pauvre Pierrot';".as[Title]
        databaseCommands.run[Vector[Title]](sqlQuery)        
    }
}
