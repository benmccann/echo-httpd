/*
 * Copyright © 2014 Typesafe, Inc. All rights reserved.
 */

package de.heikoseeberger.echohttpd

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.server.ScalaRoutingDSL
import akka.io.IO
import akka.pattern.ask
import akka.stream.FlowMaterializer
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

class EchoHttpd(interface: String, port: Int)(implicit system: ActorSystem, bindTimeout: Timeout)
    extends ScalaRoutingDSL {

  import system.dispatcher

  private implicit val materializer = FlowMaterializer()

  def run(): Unit = {
    def onBindSuccess(serverBinding: Http.ServerBinding) = {
      println(s"Listening on $interface:$port")
      println(s"To shutdown, send GET request to http://$interface:$port/shutdown")
      handleConnections(serverBinding).withRoute(route)
    }

    def onBindFailure(error: Throwable) = {
      println(f"Could not bind to $interface:$port!%n$error")
      system.shutdown()
    }

    IO(Http)
      .ask(Http.Bind(interface, port))
      .mapTo[Http.ServerBinding]
      .onComplete {
        case Success(serverBinding) => onBindSuccess(serverBinding)
        case Failure(error)         => onBindFailure(error)
      }
  }

  private def route =
    // format: OFF
    get {
      pathSuffix("shutdown") {
        complete {
          system.scheduler.scheduleOnce(500 millis)(system.shutdown())
          "Shutting down ..."
        }
      } ~
      pathSuffix("who") {
        complete {
          s"$interface:$port"
        }
      } ~
      path(RestPath) { path =>
        complete {
          path.toString()
        }
      }
    } // format: ON
}
