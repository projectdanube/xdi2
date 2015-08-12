package xdi2.server.impl.embedded;

import org.eclipse.jetty.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.XDIServer;
import xdi2.transport.impl.http.impl.servlet.EndpointServlet;

public class XDIEmbeddedServer extends Server implements XDIServer {

	public static final String FALLBACK_APPLICATIONCONTEXT = "fallback-applicationContext.xml";
	public static final String FALLBACK_JETTY_APPLICATIONCONTEXT = "fallback-jetty-applicationContext.xml";

	private EndpointServlet endpointServlet;

	public XDIEmbeddedServer() {

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
	public static XDIEmbeddedServer newServer(ApplicationContext applicationContext) {

		if (applicationContext == null) throw new NullPointerException();

		XDIEmbeddedServer endpointServerEmbedded = (XDIEmbeddedServer) applicationContext.getBean("EndpointServerEmbedded");

		return endpointServerEmbedded;
	}

	public static XDIEmbeddedServer newServer(Resource... resources) {

		return newServer(makeApplicationContext(resources));
	}

	public static XDIEmbeddedServer newServer(Resource applicationContextResource, Resource jettyApplicationContextResource) {

		if (applicationContextResource == null) applicationContextResource = fallbackApplicationContextResource();
		if (jettyApplicationContextResource == null) jettyApplicationContextResource = fallbackJettyApplicationContextResource();

		return newServer(new Resource[] { applicationContextResource, jettyApplicationContextResource });
	}

	public static XDIEmbeddedServer newServer() {

		return newServer(null, null);
	}

	private static Resource fallbackApplicationContextResource() {

		return new UrlResource(XDIEmbeddedServer.class.getResource(FALLBACK_APPLICATIONCONTEXT));
	}

	private static Resource fallbackJettyApplicationContextResource() {

		return new UrlResource(XDIEmbeddedServer.class.getResource(FALLBACK_JETTY_APPLICATIONCONTEXT));
	}

	private static ApplicationContext makeApplicationContext(Resource... resources) {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();
		applicationContext.load(resources);
		applicationContext.refresh();

		return applicationContext;
	}
}
