package lunatech.serializer

//#json-formats
import spray.json.DefaultJsonProtocol

import lunatech.actors.ImdbRegistryActor._
import lunatech.models.{InfoTitle, Informations, ErrorDescription, Title, Principals, Crew, Name, Rating, RatedMovie, TopRatedMovies}
import lunatech.models.RatedMovie


object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val titleJsonFormat = jsonFormat7(Title)
  implicit val nameJsonFormat = jsonFormat5(Name)
  implicit val castJsonFormat = jsonFormat4(Principals)
  implicit val crewJsonFormat = jsonFormat2(Crew)
  implicit val infoTitleJsonFormat = jsonFormat3(InfoTitle)
  implicit val infosTitleJsonFormat = jsonFormat1(Informations)

  implicit val ratingTitleJsonFormat = jsonFormat2(Rating)
  implicit val ratedMovieTitleJsonFormat = jsonFormat2(RatedMovie)
  implicit val topRatedMoviesTitleJsonFormat = jsonFormat1(TopRatedMovies)
  implicit val errorJsonFormat = jsonFormat1(ErrorDescription)

}
//#json-formats
