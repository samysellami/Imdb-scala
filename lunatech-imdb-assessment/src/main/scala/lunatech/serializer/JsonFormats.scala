package lunatech.serializer

//#json-formats
import spray.json.DefaultJsonProtocol

import lunatech.actors.ImdbRegistry.ActionPerformed
import lunatech.actors.ImdbRegistry._
import lunatech.models.{Title, Titles, Error}


object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val titleJsonFormat = jsonFormat3(Title)
  implicit val titlesJsonFormat = jsonFormat1(Titles)
  implicit val errorJsonFormat = jsonFormat1(Error)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
