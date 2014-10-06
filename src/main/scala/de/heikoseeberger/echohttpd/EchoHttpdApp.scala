/*
 * Copyright © 2014 Typesafe, Inc. All rights reserved.
 */

package de.heikoseeberger.echohttpd

import akka.actor.ActorSystem
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{ Duration, DurationInt, FiniteDuration }
import scopt.{ OptionParser, Read }

object EchoHttpdApp {

  private case class Conf(
    interface:   String         = defaultInterface,
    port:        Int            = defaultPort,
    bindTimeout: FiniteDuration = defaultBindTimeout
  )

  private implicit val finiteDurationRead: Read[FiniteDuration] =
    Read.reads(millis => Duration(millis.toLong, TimeUnit.MILLISECONDS))

  private val opt = """(\S+)=(\S+)""".r

  private val defaultInterface = InetAddress.getLocalHost.getHostAddress

  private val defaultPort = 9000

  private val defaultBindTimeout = 1000.millis

  private val parser =
    new OptionParser[Conf]("echo-httpd") {
      head("echo-httpd")
      help("help")
        .text("prints this usage text")
      opt[String]('i', "interface")
        .text(s"the IP interface to bind to; defaults to $defaultInterface")
        .optional()
        .action((interface, conf) => conf.copy(interface = interface))
      opt[Int]('p', "port")
        .text(s"the IP port to bind to; defaults to $defaultPort")
        .optional()
        .action((port, conf) => conf.copy(port = port))
      opt[FiniteDuration]('t', "bind-timeout")
        .text(s"the bind timeout; defaults to ${defaultBindTimeout.toMillis} millis")
        .optional()
        .action((bindTimeout, conf) => conf.copy(bindTimeout = bindTimeout))
    }

  def main(args: Array[String]): Unit = {
    for (conf <- parser.parse(args, Conf())) {
      import conf._
      val system = ActorSystem()
      new EchoHttpd(interface, port)(system, bindTimeout).run()
      system.awaitTermination()
    }
  }
}
