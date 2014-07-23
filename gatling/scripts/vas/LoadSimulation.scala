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
 * Load simulation of the Akka VAS test
 */
class LoadSimulation extends Simulation {
      
 val loadTransferChain = 
    repeat(Config.runs) {
      exec(session => {
        val clas   = Utils.randInt(Config.clas) + 1
        val from   = Utils.randInt(Config.merchants + 1, Config.merchants + Config.accounts + 1)
        val to     = Utils.randInt(1, Config.merchants + 1)
        val amount = Utils.randInt(1, 1000)
        session
          .setAttribute("clasId", Utils.clasID(clas))
          .setAttribute("from", from)
          .setAttribute("to", to)
          .setAttribute("amount", amount)
      })
      .exec(http("transfer").post("transfer/${clasId}/${from}/${to}/${amount}").asJSON.check(status.is(200)))
    }

 val loadBalanceChain = 
    repeat(Config.runs) {
      exec(session => {
        val clas    = Utils.randInt(Config.clas) + 1
        val account = Utils.randInt(Config.merchants + Config.accounts) + 1
        session
          .setAttribute("clasId", Utils.clasID(clas))
          .setAttribute("accountId", account)
      })
      .exec(http("balance").get("account/${clasId}/${accountId}/balance").asJSON.check(status.is(200)))
    }
    
  val transferScn = scenario("Load Transfer Test").exec(loadTransferChain)
  val balanceScn = scenario("Load Balance Test").exec(loadBalanceChain)
     
  setUp(
    transferScn.users(Config.usersTransfer).ramp(Config.ramp).protocolConfig(Config.httpConf),
    balanceScn.users(Config.usersBalance).ramp(Config.ramp).protocolConfig(Config.httpConf)
  )
}