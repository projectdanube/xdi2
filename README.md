<a href="http://projectdanube.org/" target="_blank"><img src="http://peacekeeper.github.com/xdi2/images/projectdanube_logo.png" align="right"></a>
<img src="http://peacekeeper.github.com/xdi2/images/logo64.png"><br>
[![Build Status](https://secure.travis-ci.org/peacekeeper/xdi2.png)](http://travis-ci.org/peacekeeper/xdi2)

[XDI2](http://github.com/peacekeeper/xdi2) is a general purpose XDI library for Java, supporting both a traditional client/server model and distributed peer-to-peer data exchange. 

It is the basis for [Project Danube](http://www.projectdanube.org/).

A sample deployment of XDI2 is available at http://xdi2.projectdanube.org.

### Components

* [xdi2-core](https://github.com/peacekeeper/xdi2/wiki/xdi2-core) - Implementation of the XDI graph model and basic features [.jar]
* [xdi2-messaging](https://github.com/peacekeeper/xdi2/wiki/xdi2-messaging) - Implementation of XDI messaging functionality [.jar]
* [xdi2-client](https://github.com/peacekeeper/xdi2/wiki/xdi2-client) - An XDI client that can send messages to an XDI endpoint [.jar]
* [xdi2-server](https://github.com/peacekeeper/xdi2/wiki/xdi2-server) - An XDI server exposing XDI endpoints that can process incoming XDI messages [.jar]
* [xdi2-server-standalone](https://github.com/peacekeeper/xdi2/wiki/xdi2-server-standalone) - The XDI server as a standalone application [.jar]
* [xdi2-server-war](https://github.com/peacekeeper/xdi2/wiki/xdi2-server-war) - The XDI server as a web application [.war]
* [xdi2-webtools](https://github.com/peacekeeper/xdi2/wiki/xdi2-webtools) - A collection of web-based XDI tools for testing [.war]
* [xdi2-xri2xdi](https://github.com/peacekeeper/xdi2/wiki/xdi2-xri2xdi) - A proxy XDI discovery service based on the global XRI registry [.war]
* [xdi2-p2p](https://github.com/peacekeeper/xdi2/wiki/xdi2-p2p) - Code for peer-to-peer XDI data exchange [.jar]
* [xdi2-samples](https://github.com/peacekeeper/xdi2/wiki/xdi2-samples) - Various samples on how to work with the XDI2 library [.jar]

### How to build

Just run

    mvn clean install

To build all components.

### How to run the XDI web tools

    cd webtools
    mvn jetty:run

Then go to:

    http://localhost:8080/

### Community

Javadoc: http://peacekeeper.github.com/xdi2/apidocs

Google Group: http://groups.google.com/group/xdi2

Changelog: https://github.com/peacekeeper/xdi2/blob/master/CHANGELOG

### Extensions

* [xdi2-connector-facebook](https://github.com/peacekeeper/xdi2-connector-facebook) - Facebook -> XDI Connector
* [xdi2-connector-personal](https://github.com/peacekeeper/xdi2-connector-personal) - Personal.com -> XDI Connector

### Tests

See [Testing](https://github.com/peacekeeper/xdi2/wiki/Testing) for a description of unit tests, etc.
