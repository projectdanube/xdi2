package xdi2.server;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.plugins.PluginsLoader;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.servlet.EndpointServlet;

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
	public static EndpointServerEmbedded newServer(ApplicationContext applicationContext) throws Xdi2ServerException {

		if (applicationContext == null) throw new NullPointerException();

		EndpointServerEmbedded endpointServer = (EndpointServerEmbedded) applicationContext.getBean("EndpointServer");

		return endpointServer;
	}

	public static EndpointServerEmbedded newServer(Resource... resources) throws Xdi2ServerException {

		try {

			PluginsLoader.loadPlugins();
		} catch (IOException ex) {

			throw new Xdi2ServerException("Cannot load plugins: " + ex.getMessage(), ex);
		}

		return newServer(makeApplicationContext(resources));
	}

	public static EndpointServerEmbedded newServer(Resource applicationContextResource, Resource jettyApplicationContextResource) throws Xdi2ServerException {

		if (applicationContextResource == null) applicationContextResource = fallbackApplicationContextResource();
		if (jettyApplicationContextResource == null) jettyApplicationContextResource = fallbackJettyApplicationContextResource();

		return newServer(new Resource[] { applicationContextResource, jettyApplicationContextResource });
	}

	public static EndpointServerEmbedded newServer() throws Xdi2ServerException {

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
