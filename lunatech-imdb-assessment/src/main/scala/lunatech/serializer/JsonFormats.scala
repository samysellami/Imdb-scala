package lunatech.serializer

//#json-formats
import spray.json.DefaultJsonProtocol

import lunatech.actors.ImdbRegistry._
import lunatech.models.{InfoTitle, Infos, ErrorDescription, Title, Cast, Crew, Name}


object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val titleJsonFormat = jsonFormat7(Title)
  implicit val nameJsonFormat = jsonFormat5(Name)
  implicit val castJsonFormat = jsonFormat4(Cast)
  implicit val crewJsonFormat = jsonFormat2(Crew)
  implicit val infoTitleJsonFormat = jsonFormat2(InfoTitle)
  implicit val infosTitleJsonFormat = jsonFormat1(Infos)
  implicit val errorJsonFormat = jsonFormat1(ErrorDescription)

}
//#json-formats
