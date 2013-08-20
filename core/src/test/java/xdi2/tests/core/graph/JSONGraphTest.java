package xdi2.tests.core.graph;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.JSONGraphFactory;

public class JSONGraphTest extends AbstractGraphTest {

	private JSONGraphFactory graphFactory = new JSONGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String filename) {

				return filename.endsWith(".json");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return this.graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		return graph;
	}
}
