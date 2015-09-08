<a href="http://projectdanube.org/" target="_blank"><img src="http://projectdanube.github.com/xdi2/images/projectdanube_logo.png" align="right"></a>
<img src="http://projectdanube.github.com/xdi2/images/logo64.png"><br>

| Current version under development: 0.8-SNAPSHOT&nbsp;&nbsp;[![Build Status](https://secure.travis-ci.org/projectdanube/xdi2.png)](http://travis-ci.org/projectdanube/xdi2) |
| ---- |

| Current stable release: [0.7.4](https://github.com/projectdanube/xdi2/wiki/release-0.7) | Next release: [0.8](https://github.com/projectdanube/xdi2/wiki/release-0.8) | [More about releases and branches](https://github.com/projectdanube/xdi2/wiki/Releases-and-Branches) |
| ---- | ---- | ---- |

XDI2 (“XDI Two”) is a general-purpose, lightweight and modular Java implementation of XDI specifications.

Website: https://xdi2.org/. Sample deployment: https://server.xdi2.org/.

Jenkins: https://jenkins.xdi2.org/. Artifactory: https://artifactory.xdi2.org/.

### Information

Examples:

[xdi2-example-core](https://github.com/projectdanube/xdi2-example-core), [xdi2-example-client](https://github.com/projectdanube/xdi2-example-client), [xdi2-example-messaging](https://github.com/projectdanube/xdi2-example-messaging), [xdi2-example-server](https://github.com/projectdanube/xdi2-example-server).

Components:

* [xdi2-core](https://github.com/projectdanube/xdi2/wiki/xdi2-core) - Implementation of the XDI graph model and basic features [.jar]
* [xdi2-rdf](https://github.com/projectdanube/xdi2/wiki/xdi2-rdf) - Implementation of the XDI/RDF compatibility layer [.jar]
* [xdi2-messaging](https://github.com/projectdanube/xdi2/wiki/xdi2-messaging) - Implementation of XDI messaging functionality [.jar]
* [xdi2-client](https://github.com/projectdanube/xdi2/wiki/xdi2-client) - An XDI client can send messages to an XDI server over HTTP(S) [.jar]
* [xdi2-client-websocket](https://github.com/projectdanube/xdi2/wiki/xdi2-client-websocket) - An XDI client can send messages to an XDI server over WebSocket [.jar]
* [xdi2-transport](https://github.com/projectdanube/xdi2/wiki/xdi2-transport) - Common transport functionality for receiving and processing XDI messages [.jar]
* [xdi2-transport-uri](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-uri) - Common transport functionality for URI-based transports [.jar]
* [xdi2-transport-http](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-http) - An HTTP transport that exposes XDI endpoints at URIs [.jar]
* [xdi2-transport-websocket](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-websocket) - A WebSocket transport that exposes XDI endpoints at URIs [.jar]
* [xdi2-transport-local](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-local) - A local transport that executes messages directly against a local graph [.jar]
* [xdi2-server](https://github.com/projectdanube/xdi2/wiki/xdi2-server) - The XDI server that provides the HTTP and WebSocket transport [.jar]
* [xdi2-server-embedded](https://github.com/projectdanube/xdi2/wiki/xdi2-server-embedded) - The XDI server embedded in another application [.jar]
* [xdi2-server-standalone](https://github.com/projectdanube/xdi2/wiki/xdi2-server-standalone) - The XDI server as a standalone application [.jar]
* [xdi2-server-war](https://github.com/projectdanube/xdi2/wiki/xdi2-server-war) - The XDI server as a web application [.war]
* [xdi2-webtools](https://github.com/projectdanube/xdi2/wiki/xdi2-webtools) - A collection of web-based XDI tools for testing [.war]

### How to build

Just run

    mvn clean install

To build all components.

### How to run the XDI web tools

    cd webtools
    mvn jetty:run

Then go to:

    http://localhost:8080/

### How to use

Maven repository for releases:

	<repositories>
		<repository>
			<id>XDI2</id>
			<name>XDI2-releases</name>
			<url>https://artifactory.xdi2.org/xdi2-releases-local</url>
		</repository>
	</repositories>

Maven repository for snapshots:

	<repositories>
		<repository>
			<id>XDI2</id>
			<name>XDI2-releases</name>
			<url>https://artifactory.xdi2.org/xdi2-snapshot-local</url>
		</repository>
	</repositories>

Maven dependencies:

	<dependencies>
		<dependency>
			<groupId>xdi2</groupId>
			<artifactId>xdi2-client</artifactId>
			<version>... version here ...</version>
			<scope>compile</scope>
		</dependency>
	</dependencies>

See also https://bintray.com/projectdanube/maven/xdi2/view

### Plugins

See [here](https://github.com/projectdanube/xdi2/wiki/XDI2-plugins) for information about XDI2 plugins.

### Tests

See [here](https://github.com/projectdanube/xdi2/wiki/Testing) for a description of unit tests.

### Community

Website: https://xdi2.org/

Google Group: http://groups.google.com/group/xdi2

Weekly Call: [Thursdays at 4pm US Eastern Time](https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call)

IRC: [irc://irc.freenode.net:6667/xdi](http://webchat.freenode.net?randomnick=1&channels=%23xdi)

Javadoc: http://projectdanube.github.io/xdi2/apidocs/

### Related Projects

* XDI Graph Editor by Neustar: https://github.com/neustar/xdi-grapheditor.git
* REST Wrapper for XDI2 signature creation and validation: https://github.com/Meeco/rest-xdi
* XDI authentication for spring-security: https://github.com/andre-pt/xdi-springsecurity
