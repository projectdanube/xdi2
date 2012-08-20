package xdi2.server;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jetty.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.exceptions.Xdi2ServerException;

public class EndpointServer extends Server {

	public static final String FALLBACK_APPLICATIONCONTEXT = "fallback-applicationContext.xml";
	public static final String FALLBACK_JETTY_APPLICATIONCONTEXT = "fallback-jetty-applicationContext.xml";

	private EndpointFilter endpointFilter;
	private EndpointServlet endpointServlet;

	public EndpointServer() {

		this.endpointFilter = null;
		this.endpointServlet = null;

		this.setStopAtShutdown(true);
	}

	public EndpointFilter getEndpointFilter() {

		return this.endpointFilter;
	}

	public void setEndpointFilter(EndpointFilter endpointFilter) {

		this.endpointFilter = endpointFilter;
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}

	/*	public static EndpointServer newServer(EndpointFilter endpointFilter, EndpointServlet endpointServlet, String contextPath, String servletPath, int port) {

		if (! contextPath.endsWith("/")) contextPath += "/";
		if (! servletPath.endsWith("/")) servletPath += "/";

		EndpointServer endpointServer = new EndpointServer();
		endpointServer.setEndpointFilter(endpointFilter);
		endpointServer.setEndpointServlet(endpointServlet);

		Connector connector = new org.eclipse.jetty.server.nio.SelectChannelConnector();
		connector.setPort(port);

		FilterHolder filterHolder = new FilterHolder();
		filterHolder.setFilter(endpointFilter);
		filterHolder.setName("EndpointFilter");

		ServletHolder servletHolder = new ServletHolder();
		servletHolder.setServlet(endpointServlet);
		servletHolder.setName("EndpointServlet");

		ServletMapping servletMapping = new ServletMapping();
		servletMapping.setServletName("EndpointServlet");
		servletMapping.setPathSpec(servletPath + "*");

		FilterMapping filterMapping = new FilterMapping();
		filterMapping.setFilterName("EndpointFilter");
		filterMapping.setServletName("EndpointServlet");

		ServletHandler servletHandler = new ServletHandler();
		servletHandler.setServlets(new ServletHolder[] { servletHolder });
		servletHandler.setFilters(new FilterHolder[] { filterHolder });
		servletHandler.setServletMappings(new ServletMapping[] { servletMapping });
		servletHandler.setFilterMappings(new FilterMapping[] { filterMapping });

		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.setServletHandler(servletHandler);
		servletContextHandler.setContextPath(contextPath);

		endpointServer.setHandler(servletContextHandler);

		return endpointServer;
	}*/

	/**
	 * Generates a new XDI2 server from an application context.
	 */
	public static EndpointServer newServer(ApplicationContext applicationContext) throws Xdi2ServerException {

		if (applicationContext == null) throw new NullPointerException();

		EndpointServer endpointServer = (EndpointServer) applicationContext.getBean("EndpointServer");

		return endpointServer;
	}

	public static EndpointServer newServer(Resource... resources) throws Xdi2ServerException {

		return newServer(makeApplicationContext(resources));
	}

	public static EndpointServer newServer(Resource applicationContextResource, Resource jettyApplicationContextResource) throws Xdi2ServerException {

		if (applicationContextResource == null) applicationContextResource = fallbackApplicationContextResource();
		if (jettyApplicationContextResource == null) jettyApplicationContextResource = fallbackJettyApplicationContextResource();

		return newServer(new Resource[] { applicationContextResource, jettyApplicationContextResource });
	}

	public static EndpointServer newServer() throws Xdi2ServerException {

		return newServer(null, null);
	}

	public static void main(String... args) throws Exception {

		String applicationContextPath;
		String jettyApplicationContextPath;

		if (args.length == 2) {

			applicationContextPath = args[0];
			jettyApplicationContextPath = args[1];
		} else if (args.length == 1) {

			applicationContextPath = args[0];
			jettyApplicationContextPath = "jetty-applicationContext.xml";
		} else if (args.length == 0) {

			applicationContextPath = "applicationContext.xml";
			jettyApplicationContextPath = "jetty-applicationContext.xml";
		} else {

			usage();
			return;
		}

		File applicationContextFile = new File(applicationContextPath);
		if (! applicationContextFile.exists()) throw new FileNotFoundException(applicationContextPath + " not found");

		File jettyApplicationContextFile = new File(jettyApplicationContextPath);
		if (! jettyApplicationContextFile.exists()) throw new FileNotFoundException(jettyApplicationContextPath + " not found");

		Resource applicationContextResource = new FileSystemResource(applicationContextFile);
		Resource jettyApplicationContextResource = new FileSystemResource(jettyApplicationContextFile);

		EndpointServer endpointServer = EndpointServer.newServer(applicationContextResource, jettyApplicationContextResource);
		endpointServer.start();
	}

	private static void usage() {

		System.out.println("Usage: java -jar xdi2-server-standalone-XXX.one-jar.jar <path-to-applicationContext.xml> <path-to-jetty-applicationContext.xml>");
	}

	private static Resource fallbackApplicationContextResource() {

		return new UrlResource(EndpointServer.class.getResource(FALLBACK_JETTY_APPLICATIONCONTEXT));
	}

	private static Resource fallbackJettyApplicationContextResource() {

		return new UrlResource(EndpointServer.class.getResource(FALLBACK_APPLICATIONCONTEXT));
	}

	private static ApplicationContext makeApplicationContext(Resource... resources) {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();
		applicationContext.load(resources);
		applicationContext.refresh();

		return applicationContext;
	}
}
