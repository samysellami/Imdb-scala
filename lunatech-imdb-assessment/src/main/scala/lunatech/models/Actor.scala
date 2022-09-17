package lunatech.models

final case class Actor(nconst: String, castActors: List[Actor] = List(), var visited: Boolean = false, var prevActor: Option[Actor] = None)
