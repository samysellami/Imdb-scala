package lunatech.models

final case class Actor(nconst: String, castActors: List[Actor]= List(), visited: Int = 0)
