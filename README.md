[XDI�](http://github.com/peacekeeper/xdi2) is a general purpose XDI library for Java, supporting both a traditional client/server model and distributed peer-to-peer data exchange. [![Build Status](https://secure.travis-ci.org/LockerProject/Locker.png)](http://travis-ci.org/LockerProject/Locker)

It is the basis for [Project Danube](http://www.projectdanube.org/).

A sample deployment is available at [[http://xdi2.projectdanube.org]].

### Components

* [https://github.com/peacekeeper/xdi2/wiki/xdi2-core](xdi2-core)
* [https://github.com/peacekeeper/xdi2/wiki/xdi2-messaging](xdi2-messaging)
* [https://github.com/peacekeeper/xdi2/wiki/xdi2-client](xdi2-client)
* [https://github.com/peacekeeper/xdi2/wiki/xdi2-server](xdi2-server)
* [https://github.com/peacekeeper/xdi2/wiki/xdi2-webtools](xdi2-webtools)
* [https://github.com/peacekeeper/xdi2/wiki/xdi2-p2p](xdi2-p2p)

### How to build

Just run

    mvn clean install

To build all components.

### How to run the XDI web tools

    cd webtools
    mvn jetty:run

Then go to

    http://localhost:8080/


### How to run the standalone XDI server

    cd server
    mvn jetty:run

Then XDI endpoints are available at:

    http://localhost:8080/xdi/mem-graph/
    http://localhost:8080/xdi/bdb-graph/
    http://localhost:8080/xdi/file-graph/
