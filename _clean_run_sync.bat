del akka.log
del server.log
rem call mvn clean package -DskipTests
java -jar target\vas-1.1.jar --vas.async=false