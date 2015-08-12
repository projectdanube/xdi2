package xdi2.server.impl.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import xdi2.core.plugins.PluginsLoader;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.impl.embedded.XDIEmbeddedServer;

public class XDIStandaloneServer {

	/*
	 * Main and usage
	 */
	
	public static void main(String[] args) throws Exception {

		// check arguments

		String applicationContextPath;
		String serverApplicationContextPath;

		if (args.length == 2) {

			applicationContextPath = args[0];
			serverApplicationContextPath = args[1];
		} else if (args.length == 1) {

			applicationContextPath = args[0];
			serverApplicationContextPath = "server-applicationContext.xml";
		} else if (args.length == 0) {

			applicationContextPath = "applicationContext.xml";
			serverApplicationContextPath = "server-applicationContext.xml";
		} else {

			usage();
			return;
		}

		// load plugins

		try {

			PluginsLoader.loadPlugins();
		} catch (IOException ex) {

			throw new Xdi2ServerException("Cannot load plugins: " + ex.getMessage(), ex);
		}

		// start the server

		File applicationContextFile = new File(applicationContextPath);
		if (! applicationContextFile.exists()) throw new FileNotFoundException(applicationContextPath + " not found");

		File serverApplicationContextFile = new File(serverApplicationContextPath);
		if (! serverApplicationContextFile.exists()) throw new FileNotFoundException(serverApplicationContextPath + " not found");

		Resource applicationContextResource = new FileSystemResource(applicationContextFile);
		Resource serverApplicationContextResource = new FileSystemResource(serverApplicationContextFile);

		XDIEmbeddedServer endpointServer = XDIEmbeddedServer.newServer(applicationContextResource, serverApplicationContextResource);
		endpointServer.start();
	}

	private static void usage() {

		System.out.println("Usage: java -jar xdi2-server-standalone-XXX.one-jar.jar [path-to-applicationContext.xml] [path-to-server-applicationContext.xml]");
	}
}
