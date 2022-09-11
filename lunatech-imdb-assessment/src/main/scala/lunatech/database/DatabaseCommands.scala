package lunatech.database

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.{JdbcBackend, SQLActionBuilder}

import scala.concurrent.Future


class DatabaseCommands {
  lazy val db = ImdbDatabase.createDatabase()

  def runQuery(query: SQLActionBuilder): Future[Seq[String]] = {
    db.run(query.as[String])
  }

  def run[T](commands: DBIOAction[T, NoStream, Effect.All]): Future[T] = {
    db.run(commands)
  }

  def runTransactionally[T](commands: DBIOAction[T, NoStream, Effect.All]): Future[T] = {
    db.run(commands.transactionally)
  }

  def fetchLongList(query: SQLActionBuilder): Future[Vector[Long]] = {
    db.run(query.as[Long])
  }
}