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
 * Load Simulation of akka VAS
 */
object Utils {

  def clasID(a:Int) : String = "clas-" + a;  
  
  // set the seed so the result is reproducable  
  private val RNG = new Random(1234)
  
  def randInt(a:Int) = RNG.nextInt(a)    
  def randInt(a:Int, b:Int) = RNG.nextInt(b-a) + a    
}