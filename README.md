<img src="http://peacekeeper.github.com/xdi2/images/logo64.png"><br>
[![Build Status](https://secure.travis-ci.org/peacekeeper/xdi2.png)](http://travis-ci.org/peacekeeper/xdi2)

[XDI²](http://github.com/peacekeeper/xdi2) is a general purpose XDI library for Java, supporting both a traditional client/server model and distributed peer-to-peer data exchange. 

It is the basis for [Project Danube](http://www.projectdanube.org/).

A sample deployment of XDI² is available at http://xdi2.projectdanube.org.

### Components

* [xdi2-core](https://github.com/peacekeeper/xdi2/wiki/xdi2-core) - Implementation of the XDI graph model and basic features [.jar]
	* [How to start working with an XDI graph]
	* [Available backend storage for XDI graphs]
	* [How to (de-)serialize XDI graphs]
	* [How to work with multiplicity]
	* [How to work with dictionaries]
* [xdi2-messaging](https://github.com/peacekeeper/xdi2/wiki/xdi2-messaging) - Implementation of XDI messaging functionality [.jar]
	* [Overview of XDI messaging]
	* [Messaging targets]
	* [Interceptors]
	* [Error handling]
* [xdi2-client](https://github.com/peacekeeper/xdi2/wiki/xdi2-client) - An XDI client that can send messages to an XDI endpoint [.jar]
	* [How to apply XDI messages to a local XDI graph]
	* [How to send XDI messages to an XDI endpoint]
* [xdi2-server-logic](https://github.com/peacekeeper/xdi2/wiki/xdi2-server-logic) - An XDI server exposing XDI endpoints [.jar]
* [xdi2-server](https://github.com/peacekeeper/xdi2/wiki/xdi2-server) - An XDI server exposing XDI endpoints [.war]
	* [Configuration of static XDI messaging targets]
	* [Configuration of dynamic XDI messaging targets]
* [xdi2-webtools](https://github.com/peacekeeper/xdi2/wiki/xdi2-webtools) - A collection of web-based XDI tools for testing [.war]
	* [Overview of the XDI web tools]
* [xdi2-xri2xdi](https://github.com/peacekeeper/xdi2/wiki/xdi2-xri2xdi) - A proxy XDI discovery service based on the XRI registry [.war]
	* [Overview of the proxy XDI discovery service]
* [xdi2-p2p](https://github.com/peacekeeper/xdi2/wiki/xdi2-p2p) - Code for peer-to-peer XDI data exchange [.jar]
* [xdi2-samples](https://github.com/peacekeeper/xdi2/wiki/xdi2-samples) - Various samples on how to work with the XDI² library [.jar]

### How to build

Just run

    mvn clean install

To build all components.

### How to run the XDI web tools

    cd webtools
    mvn jetty:run

Then go to

    http://localhost:8080/

### How to generate Javadoc

    mvn javadoc:javadoc
    cd target/site/apidocs

### Community

Google Group: http://groups.google.com/group/xdi2

Changelog: https://github.com/peacekeeper/xdi2/blob/master/CHANGELOG

### Tests

See [Testing](https://github.com/peacekeeper/xdi2/wiki/Testing) for a description of unit tests, etc.
