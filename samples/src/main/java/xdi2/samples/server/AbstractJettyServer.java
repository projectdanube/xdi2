package xdi2.samples.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.server.EndpointServlet;

public abstract class AbstractJettyServer {

	private static final Logger log = LoggerFactory.getLogger(AbstractJettyServer.class);

	private static Server server;

	protected abstract void setup(EndpointServlet servlet) throws Exception;

	public void run() throws Exception {

		// create and setup XDI2 endpoint servlet

		EndpointServlet endpointServlet = new EndpointServlet();

		this.setup(endpointServlet);

		// add servlet and start server

		ServletContextHandler handler = new ServletContextHandler();
		handler.addServlet(new ServletHolder(endpointServlet), "/*");

		server = new Server(9090);
		server.setHandler(handler);

		log.info("Starting server...");

		server.setGracefulShutdown(3000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}
