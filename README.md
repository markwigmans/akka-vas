# akka-vas #

The source code belonging to the [blog article](http://www.chess-ix.com/blog/can-make-mysql-app-faster/ "Can you make my MySQL webapp faster?").

This is a demonstrator of using [Reactive](http://www.reactivemanifesto.org/ "Reactive Manifesto") / [Lambda Architecture](http://lambda-architecture.net/ "Lambda Architecture") ideas with a toy version of an Account System.

To use the application, perform the following steps:

- installation of [maven](http://maven.apache.org/) / [java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (>= version 7) is assumed
- install [Redis](http://redis.io/)
- install [mySQL](http://www.mysql.com/)
- update database settings in file 'src/main/resources/application.properties'
- run application via '`_run.bat`'

On a non Windows environment '`mvn spring-boot:run`' can be used instead of '`_run.bat`'. 

## Deployment ##

Perform the following steps to create an executable jar:

    mvn clean install

run application with:

    java -jar target\vas-<version>.jar

## Parameters ##

The following setttings can de done:

- `--vas.async=false` : run directly to relational database (MySQL)

- `--vas.async=true` (default), use Redis as speed layer and relational database (MySQL) as batch layer
