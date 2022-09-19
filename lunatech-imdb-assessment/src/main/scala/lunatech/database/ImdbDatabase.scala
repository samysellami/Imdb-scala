package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

/**
 * An object that define the database to connect to
 * 
 */
object  ImdbDatabase {

    def createDatabase()  = {
        Database.forConfig("postgres")
    }   
} 