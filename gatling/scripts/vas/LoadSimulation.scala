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
      
 val loadRunChain = 
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
    
  val loadScn = scenario("Load Test").exec(loadRunChain)
     
  setUp(
    loadScn.users(Config.users).ramp(Config.ramp).protocolConfig(Config.httpConf)
  )
}