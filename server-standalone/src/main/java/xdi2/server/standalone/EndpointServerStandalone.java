package xdi2.server.standalone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import xdi2.core.plugins.PluginsLoader;
import xdi2.server.embedded.EndpointServerEmbedded;
import xdi2.server.exceptions.Xdi2ServerException;

public class EndpointServerStandalone {

	public static void main(String... args) throws Exception {

		// check arguments

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

		// load plugins

		try {

			PluginsLoader.loadPlugins();
		} catch (IOException ex) {

			throw new Xdi2ServerException("Cannot load plugins: " + ex.getMessage(), ex);
		}

		// start the server

		File applicationContextFile = new File(applicationContextPath);
		if (! applicationContextFile.exists()) throw new FileNotFoundException(applicationContextPath + " not found");

		File jettyApplicationContextFile = new File(jettyApplicationContextPath);
		if (! jettyApplicationContextFile.exists()) throw new FileNotFoundException(jettyApplicationContextPath + " not found");

		Resource applicationContextResource = new FileSystemResource(applicationContextFile);
		Resource jettyApplicationContextResource = new FileSystemResource(jettyApplicationContextFile);

		EndpointServerEmbedded endpointServer = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource);
		endpointServer.start();
	}

	private static void usage() {

		System.out.println("Usage: java -jar xdi2-server-standalone-XXX.one-jar.jar [path-to-applicationContext.xml] [path-to-jetty-applicationContext.xml]");
	}
}
