package xdi2.tests.graph;

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

		File file = new File(".", "xdi2-test-graph." + id + ".properties");
		if (file.exists()) file.delete();

		graphFactory.setFile(file);
		graphFactory.setAutoSave(true);

		return graphFactory.openGraph();
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		graph.close();

		File file = new File(".", "xdi2-test-graph." + id + ".properties");

		graphFactory.setFile(file);
		graphFactory.setAutoSave(true);

		return graphFactory.openGraph();
	}
}
