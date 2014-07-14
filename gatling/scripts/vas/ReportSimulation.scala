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
class ReportSimulation extends Simulation {
  
  val stopChain =  exec(http("stop simulation").post("simulation/stop").asJSON.check(status.is(200)))

  val validateChain = 
    repeat(Config.clas, "x") {
      exec(session => {
        val clas = session.getTypedAttribute[Int]("x") + 1
        session.setAttribute("clasId", Utils.clasID(clas))
      })
      .exec(http("validate speed layer").get("clas/${clasId}/validate/speed").check(status.is(200),jsonPath("$..successful").is("true")))
      .exec(http("validate batch layer").get("clas/${clasId}/validate/batch").check(status.is(200),jsonPath("$..successful").is("true")))
      .exec(http("validate layers combined").get("clas/${clasId}/validate/insync").check(status.is(200),jsonPath("$..successful").is("true")))
    }
   
   val scn = scenario("Report").exec(stopChain,validateChain)
     
  setUp(
    scn.users(1).protocolConfig(Config.httpConf)
  )
}