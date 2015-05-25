package com.savoirtech.hecate.load.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class RecordedSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9999")
    .inferHtmlResources()
    .acceptHeader("text/plain")
    .acceptEncodingHeader("gzip, deflate, sdch")

  val repeat = System.getProperty("repeat", "1000")
  val users = System.getProperty("users", "500")
  val duration = System.getProperty("duration", "60")

  val scn = scenario("hello").repeat(repeat.toInt) {
    exec(http("request_0")
      .get("/services/hello/jcarman").check(status.is(200)).check(bodyString.is("Hello, Jim!")))
  }


  setUp(scn.inject(rampUsers(users.toInt) over (duration.toInt seconds)))
    .protocols(httpProtocol)
}