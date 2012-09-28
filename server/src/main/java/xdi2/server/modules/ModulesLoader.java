package xdi2.server.modules;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ModulesLoader implements ServletContextListener {

	public static void loadPlugins() throws IOException {

		File[] files = new File("./modules").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.endsWith(".jar");
			}
		});

		ClassLoader classLoader = new JarClassLoader(files, Thread.currentThread().getContextClassLoader());

		Thread.currentThread().setContextClassLoader(classLoader);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		try {

			loadPlugins();
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
