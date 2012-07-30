package xdi2.samples.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.server.EndpointServlet;

public class JettyEndpointServlet {

	private static final Logger log = LoggerFactory.getLogger(JettyEndpointServlet.class);

	private static Server server;

	public static void main(String[] args) throws Throwable {

		// create endpoint servlet

		EndpointServlet servlet = new EndpointServlet();

		// add messaging targets

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		BootstrapInterceptor bi = new BootstrapInterceptor();
		bi.setBootstrapOwner(new XRI3Segment("=!1111"));
		bi.setBootstrapSharedSecret("s3cr3t");
		bi.setBootstrapLinkContract(true);

		messagingTarget.getInterceptors().add(bi);

		servlet.getEndpointRegistry().mountMessagingTarget("/", messagingTarget);

		// add servlet and start server

		ServletContextHandler handler = new ServletContextHandler();
		handler.addServlet(new ServletHolder(servlet), "/*");

		server = new Server(8080);
		server.setHandler(handler);

		log.info("Starting server...");

		server.setGracefulShutdown(3000);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
}
