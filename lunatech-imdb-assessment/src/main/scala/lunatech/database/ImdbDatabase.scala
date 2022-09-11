package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

object  ImdbDatabase {

    def createDatabase()  = {
        Database.forConfig("postgres")
    }   
} 