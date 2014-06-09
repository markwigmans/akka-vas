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