package com.spandigital

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

class RestServer(implicit val system: ActorSystem,
                 implicit val executionContext: ExecutionContextExecutor) extends RestService {
  def startServer(address: String, port: Int) = {
    val bindingFuture = Http().newServerAt(address, port).bind(route)
    println(s"Server now online. \nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}

object RestServer extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val server = new RestServer()
  server.startServer("localhost", 8081)
}
