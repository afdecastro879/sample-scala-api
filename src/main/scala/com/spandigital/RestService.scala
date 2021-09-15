package com.spandigital

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, NotFound}
import spray.json.DefaultJsonProtocol._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContextExecutor, Future}

trait RestService {
  implicit val system:ActorSystem
  implicit val executionContext: ExecutionContextExecutor

  // DB Interaction
  val connectionUrl = "jdbc:postgresql://localhost/httpsrv?user=adecastro&password="
  val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")

  final case class Item(id: Long, name: String)

  class OrdersTable(tag: Tag) extends Table[Item](tag, None, "orders") {
    override def * = (id, name) <> (Item.tupled, Item.unapply)

    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name")
  }

  val ordersTable = TableQuery[OrdersTable]

  def fetchItem(itemId: Long): Future[Option[Item]] = {
    db.run(
      ordersTable.filter(_.id === itemId)
        .result.map(seq => seq.headOption)
    )
  }

  def saveOrder(items: List[Item]): Future[Option[Int]] = {
    db.run(ordersTable ++= items)
  }

  // Exception Handler
  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler { e =>
      println(e)
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = "There's an error"))
      }
    }

  implicit val itemFormat = jsonFormat2(Item)

  val route: Route = Route.seal(
    concat(
      get {
        pathPrefix("item" / LongNumber) { id =>
          val future: Future[Option[Item]] = fetchItem(id)
          onSuccess(future) {
            case Some(item) => complete(item)
            case None => complete(NotFound)
          }
        }
      },
      post {
        path("create-order") {
          entity(as[List[Item]]) { order =>
            val saved: Future[Option[Int]] = saveOrder(order)
            onSuccess(saved) { _ =>
              complete("order created")
            }
          }
        }
      },
      get {
        path("exception") {
          complete((1 / 0).toString)
        }
      }
    )
  )
}
