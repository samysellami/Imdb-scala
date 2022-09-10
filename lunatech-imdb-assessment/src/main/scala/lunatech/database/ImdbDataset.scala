package lunatech.database

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

object ImdbDataset {

    val db = Database.forConfig("postgres")
    
}
