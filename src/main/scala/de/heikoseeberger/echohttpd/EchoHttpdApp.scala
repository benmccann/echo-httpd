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
import java.net.InetAddress
import org.reactivestreams.Publisher
import scala.collection.breakOut
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success, Try }

object EchoHttpdApp {

  private val opt = """(\S+)=(\S+)""".r

  def main(args: Array[String]): Unit = {
    val opts = argsToOpts(args.toList)
    val hostname = Try(opts("hostname")).getOrElse(InetAddress.getLocalHost.getHostAddress)
    val port = Try(opts("port").toInt).getOrElse(8080)

    val system = ActorSystem()
    new EchoHttpd(hostname, port)(system, 500 millis).run()
    system.awaitTermination()
  }

  private def argsToOpts(args: Seq[String]): Map[String, String] =
    args.collect { case opt(key, value) => key -> value }(breakOut)
}

class EchoHttpd(hostname: String, port: Int)(implicit system: ActorSystem, bindTimeout: Timeout) {

  import system.dispatcher

  private implicit val materializer = FlowMaterializer()

  private val handleRequest: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/shutdown"), _, _, _) => shutdown()
    case HttpRequest(HttpMethods.GET, Uri.Path("/status"), _, _, _)   => status()
    case HttpRequest(HttpMethods.GET, uri, _, _, _)                   => echo(uri)
  }

  def run(): Unit = {
    def handleBindSuccess(connections: Publisher[Http.IncomingConnection]) = {
      println(s"Listening on $hostname:$port ...")
      Flow(connections).foreach {
        case Http.IncomingConnection(_, requests, responses) => Flow(requests).map(handleRequest).produceTo(responses)
      }
    }
    def handleBindFailure() = {
      println(s"Could not bind to $hostname:$port!")
      system.shutdown()
    }
    val bindResponse = (IO(Http) ? Http.Bind(hostname, port)).mapTo[Http.ServerBinding]
    bindResponse.onComplete {
      case Success(Http.ServerBinding(_, connections)) => handleBindSuccess(connections)
      case _                                           => handleBindFailure()
    }
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
}
