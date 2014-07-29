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
  val usersTransfer = 20
  val usersBalance = 20
  
  // Number of runs per user
  val runs = 10000
  // Ramp up of the simulation
  val ramp = 5
}