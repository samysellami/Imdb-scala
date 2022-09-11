package lunatech.serializer

//#json-formats
import spray.json.DefaultJsonProtocol

import lunatech.actors.ImdbRegistry.ActionPerformed
import lunatech.actors.ImdbRegistry._
import lunatech.models.{Title, Titles}

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(Title)
  implicit val usersJsonFormat = jsonFormat1(Titles)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
