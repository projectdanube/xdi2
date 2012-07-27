package xdi2.tests.core.graph;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.properties.PropertiesGraphFactory;

public class PropertiesGraphTest extends AbstractGraphTest {

	private static PropertiesGraphFactory graphFactory = new PropertiesGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith("xdi2-test-graph.") && name.endsWith(".properties");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		String path = "xdi2-test-graph." + id + ".properties";

		File file = new File(path);
		if (file.exists()) file.delete();

		graphFactory.setPath(path);

		return graphFactory.openGraph();
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		graph.close();

		String path = "xdi2-test-graph." + id + ".properties";

		graphFactory.setPath(path);

		return graphFactory.openGraph();
	}
}
