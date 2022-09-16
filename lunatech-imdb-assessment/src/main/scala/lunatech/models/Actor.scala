package lunatech.models

final case class Actor(nconstActor: String, castActors: List[Actor]= List(), visited: Int = 0)
