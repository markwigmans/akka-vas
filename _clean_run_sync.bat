del akka.log
del server.log
call mvn clean package -DskipTests
java -jar target\vas-0.0.1-SNAPSHOT.jar --vas.async=false