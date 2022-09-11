package lunatech.serializer

//#json-formats
import spray.json.DefaultJsonProtocol

import lunatech.actors.ImdbRegistry._
import lunatech.models.{InfoTitle, InfosTitle, ErrorDescription}


object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val titleJsonFormat = jsonFormat3(InfoTitle)
  implicit val titlesJsonFormat = jsonFormat1(InfosTitle)
  implicit val errorJsonFormat = jsonFormat1(ErrorDescription)

}
//#json-formats
