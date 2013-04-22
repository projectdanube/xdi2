package xdi2.samples.server;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.embedded.EndpointServerEmbedded;

public class ConfiguredEndpointServerSample {

	public static void main(String[] args) throws Throwable {

		// read configuration files

		Resource applicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("applicationContext.xml"));
		Resource jettyApplicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("jetty-applicationContext.xml"));

		// create the XDI2 server

		EndpointServerEmbedded endpointServer = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource);

		// start the server

		endpointServer.start();
	}
}
