/*
 * Copyright © 2014 Typesafe, Inc. All rights reserved.
 */

package de.heikoseeberger.echohttpd

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model._
import akka.io.IO
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import akka.stream.{ FlowMaterializer, MaterializerSettings }
import akka.util.Timeout
import java.net.InetAddress
import scala.concurrent.duration.DurationInt
import scala.util.Try

object EchoHttpdApp {

  def main(args: Array[String]): Unit = {
    val hostname = System.getProperty("hostname", "0.0.0.0")
    val port = Try(System.getProperty("port").toInt) getOrElse 8080
    val bindTimeout = (Try(System.getProperty("bind-timeout").toInt) getOrElse 1000).millis
    val system = ActorSystem()
    new EchoHttpd(hostname, port)(system, bindTimeout).run()
    system.awaitTermination()
  }
}

class EchoHttpd(hostname: String, port: Int)(implicit system: ActorSystem, bindTimeout: Timeout) {

  import system.dispatcher

  private val materializer = FlowMaterializer(MaterializerSettings())

  private val handleRequest: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/shutdown"), _, _, _) => shutdown()
    case HttpRequest(HttpMethods.GET, Uri.Path("/status"), _, _, _)   => status()
    case HttpRequest(HttpMethods.GET, uri, _, _, _)                   => echo(uri)
  }

  private def shutdown() = {
    system.scheduler.scheduleOnce(500 millis)(system.shutdown())
    HttpResponse(StatusCodes.OK, entity = "Shutting down ...")
  }

  private def status() = {
    val cpus = Runtime.getRuntime.availableProcessors()
    HttpResponse(StatusCodes.OK, entity = s"Number of CPUs: $cpus")
  }

  private def echo(uri: Uri) =
    HttpResponse(StatusCodes.OK, entity = uri.path.toString())

  def run(): Unit = {
    IO(Http) ? Http.Bind(hostname, port) foreach {
      case Http.ServerBinding(_, connections) =>
        Flow(connections)
          .foreach {
            case Http.IncomingConnection(_, requestPublisher, responseSubscriber) =>
              Flow(requestPublisher)
                .map(handleRequest)
                .produceTo(materializer, responseSubscriber)
          }
          .consume(materializer)
    }
  }
}
