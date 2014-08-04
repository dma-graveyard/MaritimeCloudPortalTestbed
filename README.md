MaritimeCloudPortalTestbed
==========================

(Temporar repository until portal code is ready to go public)

A tool that offers Identity & Access Management of the Maritime Cloud Services as well as management of services published in Maritime Cloud.

The live system can be found here: TBD

## Software Architecture

The MaritimeCloudPortalTestbed client is a rich client HTML/JS-application with a server side JSON webservice API. The server is currently a
Spring Boot wrapped standalone Jetty server application.

On the client side we use:

* JavaScript/HTML
* Grunt (for building)
* Twitter Bootstrap (for basic layout)
* AngularJS (for forms and calling webservices)
* JQuery (for some DOM-manipulation)
* HTML5 Application Cache
* Karma (for unit testing)

On the server side we use:

* Java 8
* Maven (for building)
* JPA(Hibernate) (for persistence)
* SpringFramework (for dependency injection)
* Jersey (for JSON-webservices)
* Shiro (for security)
* JUnit (for unit-test)
* Mockito (for mocking)


## Prerequisites ##

* Java JDK 1.8
* Maven 3.x
* ( Not in use just yet: )
** Node.js (Follow the installation instructions at http://nodejs.org)
** Grunt.js (Follow the installation instructions at http://gruntjs.com)

## Initial setup

There is currently no need of initial setup.

## Building ##

    mvn clean install


## Launch

The build produces a launchable .war-file in the /target folder. The application can be launched with:

    MaritimeCloudPortalTestbed> java -jar target/service-0.0.1-SNAPSHOT.war

or by using maven:

    MaritimeCloudPortalTestbed> mvn spring-boot:run

A local deployment will setup MaritimeCloudPortalTestbed at the following URL:

    http://localhost:8080/app/index.html

Currently only a limited set of test users exists. To gain admin rights log in with admin/test. To see an ordinary user log in with 
Tintin/test.


## Instant reload of web resources and running in exploded mode

(This works at least on NetBeans ... not sure for other ides!?)

When using NetBeans as Ide you can easily open the project as a maven project.

To launch the server from inside Netbeans, navigate to the main class 

    java.net.maritimecloud.portal.Application

and launch it. 

(Notice: On MacBooks it's adviced to launch the application in debug-mode, as for some reason the IDE is unable to kill the maven-spawned 
process afterwards.)

Also, currently a durable persistence mechanism is not implemented, and it is therefore necessary to use the in memory based implementation 
based on hashmaps. To launch this version navigate to the test class below and launch it 

    java.net.maritimecloud.portal.ApplicationInMemory


## Netbeans setup ##

Simply open the project as an existing maven-based project. Thats it - no mumbojumbo here ;)


## JavaScript Validation Errors in Netbeans & Eclipse

### Ways to avoid annoying JavaScript Validation Errors in Netbeans:

Navigate to one of the offending scripts and open it. Go to one of the offending lines and click the light-bulb in left margin. 
Choose to suppress warnings from the menu by choosing the folder level that encapsulates the offending scripts.

### Ways to avoid annoying JavaScript Validation Errors in Eclipse:

http://stackoverflow.com/questions/7102299/eclipse-javascript-validation-disabled-but-still-generating-errors


