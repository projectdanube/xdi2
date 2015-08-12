package xdi2.server.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.plugins.PluginsLoader;
import xdi2.server.XDIServer;
import xdi2.server.exceptions.Xdi2ServerException;

public abstract class XDIAbstractServer implements ApplicationContextAware, XDIServer {

	private static final Logger log = LoggerFactory.getLogger(XDIAbstractServer.class);

	public static final String FALLBACK_APPLICATIONCONTEXT = "fallback-applicationContext.xml";
	public static final String FALLBACK_SERVER_APPLICATIONCONTEXT = "fallback-server-applicationContext.xml";

	private ApplicationContext applicationContext;

	public XDIAbstractServer() {

		this.applicationContext = null;
	}

	/*
	 * Main and usage
	 */

	@SuppressWarnings("unchecked")
	public static <T extends XDIAbstractServer> void main(String[] args, Class<? extends T> clazz) throws Exception {

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

			usage(args, clazz);
			return;
		}

		// start the server

		File applicationContextFile = new File(applicationContextPath);
		if (! applicationContextFile.exists()) throw new FileNotFoundException(applicationContextPath + " not found");

		File serverApplicationContextFile = new File(serverApplicationContextPath);
		if (! serverApplicationContextFile.exists()) throw new FileNotFoundException(serverApplicationContextPath + " not found");

		Resource applicationContextResource = new FileSystemResource(applicationContextFile);
		Resource serverApplicationContextResource = new FileSystemResource(serverApplicationContextFile);

		Method method = clazz.getMethod("newServer", Resource.class, Resource.class);
		T xdiServer = (T) method.invoke(null, applicationContextResource, serverApplicationContextResource);

		xdiServer.startServer();
	}

	private static <T extends XDIAbstractServer> void usage(String[] args, Class<? extends T> clazz) {

		String filename = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();

		System.out.println("Usage: java -jar " + filename + " <path-to-applicationContext.xml> <path-to-server-applicationContext.xml>");
	}

	/*
	 * New server
	 */

	public static <T extends XDIAbstractServer> T newServer(ApplicationContext applicationContext, Class<? extends T> clazz) throws Xdi2ServerException {

		if (applicationContext == null) throw new NullPointerException();

		T xdiServer = applicationContext.getBean(clazz.getSimpleName(), clazz);

		return xdiServer;
	}

	public static <T extends XDIAbstractServer> T newServer(Resource[] resources, Class<? extends T> clazz) throws Xdi2ServerException {

		try {

			PluginsLoader.loadPlugins();
		} catch (IOException ex) {

			throw new Xdi2ServerException("Cannot load plugins: " + ex.getMessage(), ex);
		}

		return newServer(makeApplicationContext(resources), clazz);
	}

	public static <T extends XDIAbstractServer> T newServer(Resource applicationContextResource, Resource serverApplicationContextResource, Class<? extends T> clazz) throws Xdi2ServerException {

		if (applicationContextResource == null) applicationContextResource = fallbackApplicationContextResource(clazz);
		if (serverApplicationContextResource == null) serverApplicationContextResource = fallbackServerApplicationContextResource(clazz);

		return newServer(new Resource[] { applicationContextResource, serverApplicationContextResource }, clazz);
	}

	public static <T extends XDIAbstractServer> T newServer(Class<? extends T> clazz) throws Xdi2ServerException {

		return newServer(null, null, clazz);
	}

	/*
	 * Spring configuration
	 */

	private static <T extends XDIAbstractServer> Resource fallbackApplicationContextResource(Class<? extends T> clazz) {

		return new UrlResource(clazz.getResource(FALLBACK_APPLICATIONCONTEXT));
	}

	private static <T extends XDIAbstractServer> Resource fallbackServerApplicationContextResource(Class<? extends T> clazz) {

		return new UrlResource(clazz.getResource(FALLBACK_SERVER_APPLICATIONCONTEXT));
	}

	private static ApplicationContext makeApplicationContext(Resource... resources) {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();
		applicationContext.load(resources);
		applicationContext.refresh();

		return applicationContext;
	}

	/*
	 * Instance methods
	 */

	protected void cleanup() {

		if (log.isInfoEnabled()) log.info("Cleaning up...");

		if (this.getApplicationContext() instanceof ConfigurableApplicationContext) 
			((ConfigurableApplicationContext) this.getApplicationContext()).close();
	}

	/*
	 * Getters and setters
	 */

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isInfoEnabled()) log.info("Received application context " + applicationContext.getClass().getSimpleName());

		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {

		return this.applicationContext;
	}
}
