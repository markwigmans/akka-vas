Gatling is a stress testing tool. Perform the following steps to run the tests:

1) Install version 1.5.5 from  http://gatling-tool.org/ on your machine
2) Copy the Gatling scripts to directory <gatling installation dir>\user-files\simulations
3) goto <gatling installation dir>\bin
4) Update / Modify the test in file '<gatling installation dir>\user-files\simulations\vas\Config.scala
5) run .\gatling.bat -s vas.InitSimulation
6) run .\gatling.bat -s vas.LoadSimulation
7) run .\gatling.bat -s vas.ReportSimulation

The results from step 6 tell how fast the real stress test went.

The results from step 7 tell if the system (database) is still consistent. Gatling only waits 1 minute for a response, and then it concludes that it is wrong. 
The system can still be busy (check logging in server.log), so don't perform step 7 to fast after step 6.

Mark Wigmans
