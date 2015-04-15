<a href="http://projectdanube.org/" target="_blank"><img src="http://projectdanube.github.com/xdi2/images/projectdanube_logo.png" align="right"></a>
<img src="http://projectdanube.github.com/xdi2/images/logo64.png"><br>

| Current snapshot: 0.7-SNAPSHOT&nbsp;&nbsp;[![Build Status](https://secure.travis-ci.org/projectdanube/xdi2.png)](http://travis-ci.org/projectdanube/xdi2) | Stable release: [0.6](https://github.com/projectdanube/xdi2/wiki/release-0.6) | Next release: [0.7](https://github.com/projectdanube/xdi2/wiki/release-0.7) |
| ---- | ---- | ---- |
| [Release and Branch History](https://github.com/projectdanube/xdi2/wiki/Release-and-Branch-History) |

XDI2 (“XDI Two”) is a general-purpose, lightweight and modular Java implementation of XDI specifications.

Website: https://xdi2.org/, sample deployment: https://server.xdi2.org/

### Components

* [xdi2-core](https://github.com/projectdanube/xdi2/wiki/xdi2-core) - Implementation of the XDI graph model and basic features [.jar]
* [xdi2-messaging](https://github.com/projectdanube/xdi2/wiki/xdi2-messaging) - Implementation of XDI messaging functionality [.jar]
* [xdi2-client](https://github.com/projectdanube/xdi2/wiki/xdi2-client) - An XDI client can send messages to an XDI server, including discovery [.jar]
* [xdi2-transport](https://github.com/projectdanube/xdi2/wiki/xdi2-transport) - A transport (server) can receive XDI message and process them [.jar]
* [xdi2-transport-http](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-http) - An HTTP transport (server) that exposes XDI endpoints at URIs [.jar]
* [xdi2-transport-http-embedded](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-http-embedded) - The HTTP transport (server) embedded in another application [.jar]
* [xdi2-transport-http-standalone](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-http-standalone) - The HTTP transport (server) as a standalone application [.jar]
* [xdi2-transport-http-war](https://github.com/projectdanube/xdi2/wiki/xdi2-transport-http-war) - The HTTP transport (server) as a web application [.war]
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

### Community

Website: https://xdi2.org/

Google Group: http://groups.google.com/group/xdi2

Weekly Call: [Thursdays at 2pm US Eastern Time](https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call)

IRC: [irc://irc.freenode.net:6667/xdi](irc://irc.freenode.net:6667/xdi)

Javadoc: http://projectdanube.github.io/xdi2/apidocs/

### Related Projects

* XDI Graph Editor by Neustar: https://github.com/neustar/xdi-grapheditor.git
* REST Wrapper for XDI2 signature creation and validation: https://github.com/Meeco/rest-xdi
* XDI authentication for spring-security: https://github.com/andre-pt/xdi-springsecurity

### Plugins

See [here](https://github.com/projectdanube/xdi2/wiki/XDI2-plugins) for information about XDI2 plugins.

### Tests

See [here](https://github.com/projectdanube/xdi2/wiki/Testing) for a description of unit tests.
