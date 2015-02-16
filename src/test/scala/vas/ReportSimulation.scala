/******************************************************************************
 Copyright 2014,2015 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
******************************************************************************/
package vas 

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import scala.util.Random

/**
 * Load simulation of the Akka VAS test
 */
class ReportSimulation extends Simulation {
  
  val stopChain =  exec(http("stop simulation").post("simulation/stop").asJSON.check(status.is(200)))

  val validateChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session("x").as[Int]  + 1
        session.set("clasId", Utils.clasID(clas))
      })
      .exec(http("validate speed layer").get("clas/${clasId}/validate/speed").check(status.is(200),jsonPath("$..successful").is("true")))
      .exec(http("validate batch layer").get("clas/${clasId}/validate/batch").check(status.is(200),jsonPath("$..successful").is("true")))
      .exec(http("validate layers combined").get("clas/${clasId}/validate/insync").check(status.is(200),jsonPath("$..successful").is("true")))
    }
   
   val scn = scenario("Report").exec(stopChain,validateChain)
     
  setUp(
     scn.inject(atOnceUsers(1)).protocols(Config.httpConf)
  )
}