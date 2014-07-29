/******************************************************************************
 Copyright 2014 Mark Wigmans

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

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

import scala.util.Random

/**
 * Initialize the Akka VAS test
 */
class InitSimulation extends Simulation {
 
  val cleanChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session.getTypedAttribute[Int]("x") + 1
        session.setAttribute("clasId", Utils.clasID(clas))
      })
      .exec(http("clean clas").post("clas/${clasId}/clean").asJSON.check(status.is(200)))
    } 
 
  val clasChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session.getTypedAttribute[Int]("x") + 1
        session.setAttribute("clasId", Utils.clasID(clas))
      })
      .exec(http("create clas").post("clas/${clasId}").asJSON.check(status.is(200)))
    }
  
  val accountChain =
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session.getTypedAttribute[Int]("x") + 1
        session.setAttribute("clasId", Utils.clasID(clas))
          .setAttribute("start.accounts", Config.merchants + 1)
          .setAttribute("start.merchants", 1)
          .setAttribute("count.accounts", Config.accounts)
          .setAttribute("count.merchants", Config.merchants)
      })
      .exec(http("create merchants").post("account/${clasId}/range/from/${start.merchants}/count/${count.merchants}").asJSON.check(status.is(200)))
      .exec(http("create accounts").post("account/${clasId}/range/from/${start.accounts}/count/${count.accounts}").asJSON.check(status.is(200)))
    }  
     
  val scn = scenario("Initialize").exec(cleanChain,clasChain,accountChain)
     
  setUp(
    scn.users(1).protocolConfig(Config.httpConf)
  )      
}