<a href="http://projectdanube.org/" target="_blank"><img src="http://projectdanube.github.com/xdi2/images/projectdanube_logo.png" align="right"></a>
<img src="http://projectdanube.github.com/xdi2/images/logo64.png"><br>

| Latest release: [0.6](https://github.com/projectdanube/xdi2/releases) | Current snapshot: 0.7-SNAPSHOT&nbsp;&nbsp;[![Build Status](https://secure.travis-ci.org/projectdanube/xdi2.png)](http://travis-ci.org/projectdanube/xdi2) |
| ---- | ---- |

[XDI2](http://github.com/projectdanube/xdi2) is a general purpose XDI library for Java, supporting both a traditional client/server model and distributed peer-to-peer data exchange. 

A sample deployment of XDI2 is available at http://xdi2.projectdanube.org.

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

### Plugins

See [here](https://github.com/projectdanube/xdi2/wiki/XDI2-plugins) for information about XDI2 plugins.

| Link | Type | Description |
| ----------- | ---- | ------ |
| [xdi2-mongodb](https://github.com/projectdanube/xdi2-mongodb) | Storage | XDI Graph Storage in MongoDB
| [xdi2-redis](https://github.com/projectdanube/xdi2-redis) | Storage | XDI Graph Storage in Redis
| [xdi2-connector-facebook](https://github.com/projectdanube/xdi2-connector-facebook) | Connector | Facebook -> XDI Connector
| [xdi2-connector-personal](https://github.com/projectdanube/xdi2-connector-personal) | Connector | Personal.com -> XDI Connector
| [xdi2-connector-allfiled](https://github.com/projectdanube/xdi2-connector-allfiled) | Connector | Allfiled -> XDI Connector
| [xdi2-connector-google-calendar](https://github.com/projectdanube/xdi2-connector-google-calendar) | Connector | Google Calendar -> XDI Connector

### Feature Branches

| Information | Code | Status |
| ----------- | ---- | ------ |
| [great-symbol-shift](https://github.com/projectdanube/xdi2/wiki/great-symbol-shift) | [great-symbol-shift](https://github.com/projectdanube/xdi2/tree/great-symbol-shift) | Merged per [946b5c8](https://github.com/projectdanube/xdi2/commit/946b5c8f8d5e2eb94bfd701ce73a4969012cfa9d) on 4th Apr 2014 after [snapshot-0.2-pre-symbol.shift](https://github.com/projectdanube/xdi2/releases/tag/snapshot-0.2-pre-symbol-shift).
| [link-contract-shift](https://github.com/projectdanube/xdi2/wiki/link-contract-shift) | [link-contract-shift](https://github.com/projectdanube/xdi2/tree/link-contract-shift) | Merged per [1c1ae72](https://github.com/projectdanube/xdi2/commit/1c1ae72b5b1c56bd825f97b7769967208035e99b) on 30th May 2014 after [snapshot-0.4-pre-link-contract-shift](https://github.com/projectdanube/xdi2/releases/tag/snapshot-0.4-pre-link-contract-shift).
| [notation-shift](https://github.com/projectdanube/xdi2/wiki/notation-shift) | [notation-shift](https://github.com/projectdanube/xdi2/tree/notation-shift) | Merged per [2224dd1](https://github.com/projectdanube/xdi2/commit/2224dd1c6b5cbfdbd0058928053ffca937fa17f9) on 5th Jul 2014 after [snapshot-0.5-pre-notation-shift](https://github.com/projectdanube/xdi2/releases/tag/snapshot-0.5-pre-notation-shift).
| [no-xri](https://github.com/projectdanube/xdi2/wiki/no-xri) | [no-xri](https://github.com/projectdanube/xdi2/tree/no-xri) | Merged per [7a1194b](https://github.com/projectdanube/xdi2/commit/7a1194b2ec748d446dc6d5ca02455c02eb461223) on 24th Aug 2014 after [snapshot-0.7-pre-no-xri](https://github.com/projectdanube/xdi2/releases/tag/snapshot-0.7-pre-no-xri).
| [add-mod](https://github.com/projectdanube/xdi2/wiki/add-mod) | [add-mod](https://github.com/projectdanube/xdi2/tree/add-mod) | Active.
| [no-value-node](https://github.com/projectdanube/xdi2/wiki/no-value-node) | [no-value-node](https://github.com/projectdanube/xdi2/tree/no-value-node) | Active.

### Community

Google Group: http://groups.google.com/group/xdi2

Weekly Call: [Thursdays at 2pm US Eastern Time](https://github.com/projectdanube/xdi2/wiki/XDI2-Weekly-Call)

IRC: [irc://irc.freenode.net:6667/xdi](irc://irc.freenode.net:6667/xdi)

Javadoc: http://projectdanube.github.com/xdi2/apidocs

### Virtual machine and screencasts

To give you a quick start into XDI2, you may download a VirtualBox image with all the components, or watch a screencast.

* VirtualBox: <a href="http://files.projectdanube.org/XDI2-VirtualBox.zip">Download</a>
* Screencast: <a href="http://vimeo.com/52763525">XDI Personal Cloud Demo</a> from <a href="http://vimeo.com/user3934958">Markus Sabadello</a> on <a href="http://vimeo.com">Vimeo</a>

### Related Projects

* XDI Graph Editor by Neustar: https://github.com/neustar/xdi-grapheditor.git
* REST Wrapper for XDI2 signature creation and validation: https://github.com/Meeco/rest-xdi

### Tests

See [Testing](https://github.com/projectdanube/xdi2/wiki/Testing) for a description of unit tests, etc.
