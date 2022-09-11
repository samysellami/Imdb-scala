package lunatech.models

import scala.language.implicitConversions

object ErrorType extends Enumeration {
  case class Val(message: String, cause: Option[String] = None) extends super.Val
  type ErrorType = Val
  val internalServerError: Val = Val("There was an internal server error.")
}