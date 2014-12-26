# Gatling #

Gatling is a stress testing tool. Perform the following steps to run the tests:

1. Install version 2.1.2 from  [Gatling](]http://http://gatling.io/) on your machine
2. copy conf\gatling.conf to directory <gatling installation dir>\conf
3. Copy the Gatling scripts to directory <gatling installation dir>\user-files\simulations
4. goto <gatling installation dir>\bin
5. (Optional) Update / Modify the test configuration in file '<gatling installation dir>\user-files\simulations\vas\Config.scala
6. run .\gatling.bat -s vas.InitSimulation
7. run .\gatling.bat -s vas.LoadSimulation
8. run .\gatling.bat -s vas.ReportSimulation

The results from step 7 tell how fast the real stress test went.
The results from step 8 tell if the system (database) is still consistent.

Mark Wigmans
