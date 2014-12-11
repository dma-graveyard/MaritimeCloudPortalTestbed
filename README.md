MaritimeCloudPortalTestbed
==========================

(Temporal repository until portal code is ready to go public)

A tool that offers Identity & Access Management of the Maritime Cloud Services as 
well as management of services published in Maritime Cloud.

The live system can be found here: TBD

## Software Architecture

The MaritimeCloudPortalTestbed client is a rich client HTML/JS-application with a server side JSON 
webservice API. The server is currently a Spring Boot wrapped standalone Jetty server application.

On the client side we use:

* JavaScript/HTML
* AngularJS (for forms and calling webservices)
* Twitter Bootstrap (for basic layout)
* Grunt (for building)
* JQuery (limited use for some DOM-manipulation)
* HTML5 Application Cache
* Karma with Mocha and Chai (for unit testing)
* Protractor (for end2end testing)

On the server side we use:

* Java 8
* Maven (for building)
* JPA(Hibernate) (for persistence)
* SpringFramework (for dependency injection)
* Jersey (for JSON-webservices)
* Shiro (for security)
* JUnit (for unit-test)
* Mockito (for mocking)


## Client Architecture Structure

The client application structure tend to organize resources based on features rather than their types in line 
with the Google recommendations for Angular Applications (as outlined in: [Googles 
recommendations](https://docs.google.com/document/d/1XXMvReO8-Awi1EZXAXS4PzDzdNvV6pGcuaF4Q9821Es/pub) ). 
In addition, it leans more towards the DRY'er guidelines by John Papa, as outlined 
in [John Papas Guidelines](http://www.johnpapa.net/angular-app-structuring-guidelines/). Particularly we try to 
limit redundant use of "-controller" in JS-filenames when it is obvious that this is the only kind of JS 
content in a folder. Actually, we take the LIFT guidelines a bit further. Instead of introducing a js-file for 
each controller in a module, we gather them in a single js-file. It also may contain filters and very 
specialized directives. The reasoning is that we want to limit the maintenance of dependencies in the index.html 
file as well as the shared dependencies on module names that will be scattered across many a controller- or 
filter file.   
The rule of thumb, so far, is to have a single js-file in each component named after that component have it and 
define a corresponding angular module like this "mcp.<component name>". If the file gets to big then fall back 
to the more rigid one-file-per-controller rule but share the module name. (All this may of course change again 
in the future.) 

Also, we use users.html instead of user-list.html.

Example:

```
app/
  users/
    user-details.html
    users.html
    users.js
    users_test.js
  organizations/
    ...  
```

## Prerequisites ##

* Java JDK 1.8
* Maven 3.x
* Node.js (Follow the installation instructions at http://nodejs.org)
* Bower (Follow the installation instructions at http://bower.io)

    npm install -g bower

* Karma (Follow the installation instructions at https://www.npmjs.org/package/karma)

    npm install karma

* ( Not in use just yet: )
** Grunt.js (Follow the installation instructions at http://gruntjs.com)

## Initial setup

### Install Node js modules (e.g. Karma and friends)

In order to download front-end devDependencies (for test) you need to run

    npm install

This will download external dependencies defined in 'packages.json' to the folder "src/main/webapp/app/node_modules".

### Install Selenium webdriver (for protractor to use)

The webdriver-manager is a helper tool to easily get an instance of a Selenium Server running. Use it to download the necessary binaries with:

    webdriver-manager update

To start the selenium server instance see [the end2end test section] (#end2end-test)

See https://github.com/angular/protractor/blob/master/docs/tutorial.md for more on protractor and webdriver

### Bower

In order to download front-end dependencies you need to run

    bower install

This will download external dependencies to the folder "src/main/webapp/app/bower"

### Mail SMTP

Currently the solution uses a preconfigured GMAIL account as SMTP-server when 
sending out notifications. In order to use this account you must supply the 
password in the system variable called:

    mail.smtp.password=<my_secret>

When running in context of the test configuration () and this variable is unset 
the system will fallback to echoing the mail messages to the console, which may 
come in handy on buildservers and when testing locally

To change the configuration to another mail account please refer to the settings 
file "src/main/resources/application.properties".

## Building ##

    mvn clean install

## Testing ##

### Unit tests

Karma is used for unit-testing of the client. To launch karma during development, simply run

    karma start

### End2End test

Protractor is used for "end-to-end" test of the client. To run the protractor tests two steps must be completed:

Start the selenium server

    webdriver-manager start

or

    ./node_modules/protractor/bin/webdriver-manager start

depending on where you installed the webdriver.

Use CTRL-C to stop it again when you'redone with testing. 

Run the jasmine based tests

    ./node_modules/.bin/protractor

This will use the default protractor configuration 'protractor.conf.js'

To run the cucumber scripts use instead:

    ./node_modules/.bin/protractor ./src/test/specs/cucumber.conf.js

## Launch

The build produces a executable .war-file in the /target folder. The application can be launched with:

    java -jar target/maritimecloud-portal-0.0.1-SNAPSHOT.war

or by using maven:

    mvn spring-boot:run

A local deployment will setup MaritimeCloudPortalTestbed at the following URL:

    http://localhost:8080/app/index.html

### Login

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


## Deploy

### Prepare deployment GIT project

To get the WAR-file moved onto the target server (AWS) we use a git repository as middle station. 

Clone the deployment project https://github.com/dma-dk/enav-appsrv

Copy your prepared WAR-file into the folder enav-appsrv/mc_portal/. Eg.

     mv *.war enav-appsrv/mc_portal/ 

Commit og push

### Install and launch on target AWS server

Log on to server, fetch new version and restart the application  

    ssh enav@appsrv.e-navigation.net
    cd enav-appsrv/mc_portal
    git pull
    ./portal.sh stop
    ./portal.sh start

Follow the startup process with

    tail –f portal.log

Once up-n-running the result can be seen from the link below

### Alternatively, skip the git-part

Commit changes and await build on jenkins.

Log on to server and "reploy"

    ssh enav@appsrv.e-navigation.net
    cd enav-appsrv/mc_portal
    ./portal.sh reploy

This will download the latest build from jenkins and restart the server, hence short-cutting the git-commit-push-and-pull steps. Use this 
method to avoid polluting the git deployment project repository with a lot of intermediate minor correction version 

Follow the startup process with

    tail –f portal.log

Once up-n-running the result can be seen from the link below

### Demo [Maritime Cloud Portal] (http://portal.maritimecloud.net/app/index.html)

Login with with "Tintin" or "admin". Password is "test" in both cases.

## Netbeans setup ##

Simply open the project as an existing maven-based project. Thats it - no mumbojumbo here ;)


## JavaScript Validation Errors in Netbeans & Eclipse

### Ways to avoid annoying JavaScript Validation Errors:

#### in Netbeans:

Navigate to one of the offending scripts and open it. Go to one of the offending lines and click the light-bulb in left margin. 
Choose to suppress warnings from the menu by choosing the folder level that encapsulates the offending scripts.

#### in Eclipse:

http://stackoverflow.com/questions/7102299/eclipse-javascript-validation-disabled-but-still-generating-errors


# REST API

### The REST-alike Generic Commands API

Examples:

    # List all supported commands
    curl http://localhost:8080/rest/api/command

    # Create an Organization
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=CreateOrganizationCommand" -d '{"organizationId":{"identifier":"AN_ORG_ID"},"name":"A_NAME","summary":"A_SUMMARY"}' -X POST

    # Rename organization and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeOrganizationNameAndSummaryCommand" -d '{"organizationId":{"identifier":"AN_ORG_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT

    # Prepare a service specification 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=PrepareServiceSpecificationCommand" -d '{"ownerId":{"identifier":"AN_ORG_ID"}, "serviceSpecificationId":{"identifier":"A_SPEC_ID"}, "name":"A_NAME","summary":"A_SUMMARY fail"}' -X POST

    # Rename service specification and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeServiceSpecificationNameAndSummaryCommand" -d '{"serviceSpecificationId":{"identifier":"A_SPEC_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT

    # Provide a service instance (with no coverage!!!)
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ProvideServiceInstanceCommand" -d '{"providerId":{"identifier":"AN_ORG_ID"},"specificationId":{"identifier":"A_SPEC_ID"},"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"name":"A_NAME","summary":"A_SUMMARY","coverage":null}' -X POST

    # Rename service instance and change summary 
    curl http://localhost:8080/rest/api/command -H "Content-Type: application/json;domain-model=ChangeServiceInstanceNameAndSummaryCommand" -d '{"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT



### The (more real REST) Commands API

Examples:

    #TODO: add list of commands bound to its aggregates, e.g.
    #curl http://localhost:8080/rest/api/org/dma/AN_ORG_ID -H "Content-Type: application/json;domain-model=ChangeOrganizationNameAndSummaryCommand" -d '{"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT    

    # Rename service instance and change summary 
    curl http://localhost:8080/rest/api/org/dma/si/AN_INSTANCE_ID -H "Content-Type: application/json;domain-model=ChangeServiceInstanceNameAndSummaryCommand" -d '{"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"name":"ANOTHER_NAME","summary":"ANOTHER_SUMMARY"}' -X PUT

    # Change coverage to a circle:
    curl http://localhost:8080/rest/api/org/dma/si/AN_INSTANCE_ID -H "Content-Type: application/json;domain-model=ChangeServiceInstanceCoverageCommand" -d '{"serviceInstanceId":{"identifier":"AN_INSTANCE_ID"},"coverage":[{"type":"circle","center-latitude":55.8444821875883,"center-longitude":11.788330078125,"radius":87521.03421291267}]}' -X PUT

## The Query API

    # List all specifications
    curl http://localhost:8080/rest/api/org/specification

    # List all service instances of the current organization
    curl http://localhost:8080/rest/api/org/dma/service-instance

    # List a service instance resource by its "alias path" 
    curl -X GET http://localhost:8080/rest/api/org/sma/si/anAlias


### The Almanac (the public query api)
    
    # List organizations 
    curl http://localhost:8080/rest/api/almanac/organization

    # Details of the selected organization (=dma) 
    curl http://localhost:8080/rest/api/almanac/organization/dma

    # List operational services
    curl http://localhost:8080/rest/api/almanac/operational-service

    # Details of the selected operational service
    curl http://localhost:8080/rest/api/almanac/operational-service/vsr

    # List all service specifications
    curl http://localhost:8080/rest/api/almanac/service-specification

    # Details service specification
    curl http://localhost:8080/rest/api/almanac/service-specification/a-spec

    # List all service instances
    curl http://localhost:8080/rest/api/almanac/service-instance

    # Details service instance
    curl http://localhost:8080/rest/api/almanac/service-instance/an-instance


