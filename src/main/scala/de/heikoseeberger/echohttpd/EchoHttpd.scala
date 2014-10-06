/*
 * Copyright © 2014 Typesafe, Inc. All rights reserved.
 */

package de.heikoseeberger.echohttpd

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model._
import akka.io.IO
import akka.pattern.ask
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import org.reactivestreams.Publisher
import scala.concurrent.duration.DurationInt
import scala.util.Success

class EchoHttpd(interface: String, port: Int)(implicit system: ActorSystem, bindTimeout: Timeout) {

  import system.dispatcher

  private implicit val materializer = FlowMaterializer()

  private val handleRequest: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/shutdown"), _, _, _) => shutdown()
    case HttpRequest(HttpMethods.GET, uri, _, _, _)                   => echo(uri)
  }

  def run(): Unit = {
    def onBindSuccess(connections: Publisher[Http.IncomingConnection]) = {
      println(s"Listening on $interface:$port")
      println(s"To shutdown, send GET request to http://$interface:$port/shutdown")
      Flow(connections).foreach {
        case Http.IncomingConnection(_, requests, responses) => Flow(requests).map(handleRequest).produceTo(responses)
      }
    }
    def onBindFailure() = {
      println(s"Could not bind to $interface:$port!")
      system.shutdown()
    }
    val serverBinding = (IO(Http) ? Http.Bind(interface, port)).mapTo[Http.ServerBinding]
    serverBinding.onComplete {
      case Success(Http.ServerBinding(_, connections)) => onBindSuccess(connections)
      case _                                           => onBindFailure()
    }
  }

  private def shutdown() = {
    system.scheduler.scheduleOnce(500 millis)(system.shutdown())
    HttpResponse(StatusCodes.OK, entity = "Shutting down ...")
  }

  private def echo(uri: Uri) =
    HttpResponse(StatusCodes.OK, entity = uri.path.toString())
}
