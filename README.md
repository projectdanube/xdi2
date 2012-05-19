<img src="http://peacekeeper.github.com/xdi2/images/logo64.png"><br>
[![Build Status](https://secure.travis-ci.org/peacekeeper/xdi2.png)](http://travis-ci.org/peacekeeper/xdi2)

[XDI²](http://github.com/peacekeeper/xdi2) is a general purpose XDI library for Java, supporting both a traditional client/server model and distributed peer-to-peer data exchange. 

It is the basis for [Project Danube](http://www.projectdanube.org/).

A sample deployment of XDI² is available at http://xdi2.projectdanube.org.

### Components

* [xdi2-core](https://github.com/peacekeeper/xdi2/wiki/xdi2-core)
* [xdi2-messaging](https://github.com/peacekeeper/xdi2/wiki/xdi2-messaging)
* [xdi2-client](https://github.com/peacekeeper/xdi2/wiki/xdi2-client)
* [xdi2-server-logic](https://github.com/peacekeeper/xdi2/wiki/xdi2-server-logic)
* [xdi2-server](https://github.com/peacekeeper/xdi2/wiki/xdi2-server)
* [xdi2-webtools](https://github.com/peacekeeper/xdi2/wiki/xdi2-webtools)
* [xdi2-p2p](https://github.com/peacekeeper/xdi2/wiki/xdi2-p2p)

### How to build

Just run

    mvn clean install

To build all components.

### How to run the XDI web tools

    cd webtools
    mvn jetty:run

Then go to

    http://localhost:8080/

### Tests

See [Testing](https://github.com/peacekeeper/xdi2/wiki/Testing) for a description of unit tests, etc.
