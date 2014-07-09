package vas 

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

/**
 * Configure the akka VAS simulation
 */
object Config {

  // URL of the System Under Test
	val httpConf = httpConfig.baseURL("http://localhost:8080/")
 
  // number of classes
  val clas = 10
 
  // Number of accounts per clas
  val accounts = 20000
  val merchants = 2
  
  // Number of users during the simulation
  val users = 20
  // Number of runs per user
  val runs = 10000
  // Ramp up of the simulation
  val ramp = 10
 
}