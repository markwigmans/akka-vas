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
 * Initialize the Akka VAS test
 */
class InitSimulation extends Simulation {
 
  val cleanChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session("x").as[Int]  + 1
        session.set("clasId", Utils.clasID(clas))
      })
      .exec(http("clean clas").post("clas/${clasId}/clean").asJSON.check(status.is(200)))
    } 
 
  val clasChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session("x").as[Int] + 1
        session.set("clasId", Utils.clasID(clas))
      })
      .exec(http("create clas").post("clas/${clasId}").asJSON.check(status.is(200)))
    }
  
  val accountChain =
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session("x").as[Int] + 1
        session.set("clasId", Utils.clasID(clas))
          .set("startAccounts", Config.merchants + 1)
          .set("startMerchants", 1)
          .set("countAccounts", Config.accounts)
          .set("countMerchants", Config.merchants)
      })
      .exec(http("create merchants").post("account/${clasId}/range/from/${startMerchants}/count/${countMerchants}").asJSON.check(status.is(200)))
      .exec(http("create accounts").post("account/${clasId}/range/from/${startAccounts}/count/${countAccounts}").asJSON.check(status.is(200)))
    }  
     
  val scn = scenario("Initialize").exec(cleanChain,clasChain,accountChain)
  
  setUp(
    scn.inject(atOnceUsers(1)).protocols(Config.httpConf)
  )      
}