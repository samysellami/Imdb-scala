package lunatech.models

final case class Actor(actor: String, visited: Int = 0, castActors: List[Actor]= List())
