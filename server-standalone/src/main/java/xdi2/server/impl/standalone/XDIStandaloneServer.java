package xdi2.server.impl.standalone;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import xdi2.server.XDIServer;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.impl.XDIAbstractServer;
import xdi2.transport.impl.http.impl.servlet.EndpointServlet;

public class XDIStandaloneServer extends XDIAbstractServer implements XDIServer {

	private static final Logger log = LoggerFactory.getLogger(XDIStandaloneServer.class);

	private Server server;
	private EndpointServlet endpointServlet;

	public XDIStandaloneServer() {

		this.server = null;
		this.endpointServlet = null;
	}

	/*
	 * Main and usage
	 */

	public static void main(String[] args) throws Exception {

		XDIAbstractServer.main(args, XDIStandaloneServer.class);
	}

	/*
	 * New server
	 */

	public static XDIStandaloneServer newServer(ApplicationContext applicationContext) throws Xdi2ServerException {

		return XDIAbstractServer.newServer(applicationContext, XDIStandaloneServer.class);
	}

	public static XDIStandaloneServer newServer(Resource[] resources) throws Xdi2ServerException {

		return XDIAbstractServer.newServer(resources, XDIStandaloneServer.class);
	}

	public static XDIStandaloneServer newServer(Resource applicationContextResource, Resource serverApplicationContextResource) throws Xdi2ServerException {

		return XDIAbstractServer.newServer(applicationContextResource, serverApplicationContextResource, XDIStandaloneServer.class);
	}

	public static XDIStandaloneServer newServer() throws Xdi2ServerException {

		return XDIAbstractServer.newServer(XDIStandaloneServer.class);
	}

	/*
	 * Instance methods
	 */

	@Override
	public void startServer() throws Xdi2ServerException {

		if (log.isInfoEnabled()) log.info("Server starting...");

		// set up server

		this.server.setStopAtShutdown(false);

		// register shutdown handler

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				if (XDIStandaloneServer.this.isStarted()) {

					try {

						XDIStandaloneServer.this.stopServer();
					} catch (Exception ex) {

						log.error(ex.getMessage(), ex);
					}
				}
			}
		});

		// start server

		try {

			this.server.start();
		} catch (Exception ex) {

			throw new Xdi2ServerException("Unable to start server: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void stopServer() throws Exception {

		if (this.server == null) return;

		// stop server

		this.server.stop();
		this.server = null;
		this.cleanup();

		// done

		if (log.isInfoEnabled()) log.info("Server stopped.");
	}

	@Override
	public boolean isStarted() {

		return this.server.isStarted();
	}

	/*
	 * Getters and setters
	 */

	public Server getServer() {

		return this.server;
	}

	public void setServer(Server server) {

		this.server = server;
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}
}
