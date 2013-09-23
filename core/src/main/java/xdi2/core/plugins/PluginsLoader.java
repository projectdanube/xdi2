package xdi2.core.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginsLoader {

	public static final String DEFAULT_PLUGINS_PATH = "./plugins";

	private static final Logger log = LoggerFactory.getLogger(PluginsLoader.class);

	private static File[] files;

	private PluginsLoader() { }

	public static void loadPlugins(String pluginsPath) throws IOException {

		File path = new File(pluginsPath);

		if (! path.exists()) {

			log.warn("Plugins path does not exist: " + pluginsPath);
			return;
		}

		files = path.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.endsWith(".jar");
			}
		});

		for (File file : files) {

			if (log.isInfoEnabled()) log.info("Loading XDI2 plugin: " + file.getAbsolutePath());
		}

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = new PluginClassLoader(files, currentThread.getContextClassLoader());
		currentThread.setContextClassLoader(classLoader);

		if (log.isInfoEnabled()) log.info("Set classloader for thread " + currentThread.getName() + ": " + classLoader.getClass().getCanonicalName());
	}

	public static void loadPlugins() throws IOException {

		loadPlugins(DEFAULT_PLUGINS_PATH);
	}

	public static File[] getFiles() {

		return files;
	}
}
