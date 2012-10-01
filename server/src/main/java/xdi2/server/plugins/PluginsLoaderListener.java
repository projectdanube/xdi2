package xdi2.server.plugins;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import xdi2.core.plugins.PluginsLoader;

public class PluginsLoaderListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {

			PluginsLoader.loadPlugins();
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
