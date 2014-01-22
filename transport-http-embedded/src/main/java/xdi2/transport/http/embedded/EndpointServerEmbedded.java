package xdi2.transport.http.embedded;

import org.eclipse.jetty.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.impl.servlet.EndpointServlet;

public class EndpointServerEmbedded extends Server {

	public static final String FALLBACK_APPLICATIONCONTEXT = "fallback-applicationContext.xml";
	public static final String FALLBACK_JETTY_APPLICATIONCONTEXT = "fallback-jetty-applicationContext.xml";

	private EndpointServlet endpointServlet;

	public EndpointServerEmbedded() {

		this.endpointServlet = null;

		this.setStopAtShutdown(true);
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}

	/**
	 * Generates a new XDI2 server from an application context.
	 */
	public static EndpointServerEmbedded newServer(ApplicationContext applicationContext) throws Xdi2TransportException {

		if (applicationContext == null) throw new NullPointerException();

		EndpointServerEmbedded endpointServerEmbedded = (EndpointServerEmbedded) applicationContext.getBean("EndpointServerEmbedded");

		return endpointServerEmbedded;
	}

	public static EndpointServerEmbedded newServer(Resource... resources) throws Xdi2TransportException {

		return newServer(makeApplicationContext(resources));
	}

	public static EndpointServerEmbedded newServer(Resource applicationContextResource, Resource jettyApplicationContextResource) throws Xdi2TransportException {

		if (applicationContextResource == null) applicationContextResource = fallbackApplicationContextResource();
		if (jettyApplicationContextResource == null) jettyApplicationContextResource = fallbackJettyApplicationContextResource();

		return newServer(new Resource[] { applicationContextResource, jettyApplicationContextResource });
	}

	public static EndpointServerEmbedded newServer() throws Xdi2TransportException {

		return newServer(null, null);
	}

	private static Resource fallbackApplicationContextResource() {

		return new UrlResource(EndpointServerEmbedded.class.getResource(FALLBACK_APPLICATIONCONTEXT));
	}

	private static Resource fallbackJettyApplicationContextResource() {

		return new UrlResource(EndpointServerEmbedded.class.getResource(FALLBACK_JETTY_APPLICATIONCONTEXT));
	}

	private static ApplicationContext makeApplicationContext(Resource... resources) {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();
		applicationContext.load(resources);
		applicationContext.refresh();

		return applicationContext;
	}
}
