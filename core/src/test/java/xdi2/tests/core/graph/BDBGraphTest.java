package xdi2.tests.core.graph;

import java.io.File;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.bdb.BDBGraphFactory;

public class BDBGraphTest extends AbstractGraphTest {

	private static BDBGraphFactory graphFactory = new BDBGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File path = new File(graphFactory.getDatabasePath());

		if (path.exists()) {

			for (File file : path.listFiles()) {

				file.delete();
			}
		}
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		Graph graph = graphFactory.openGraph(identifier);

		return graph;
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		graph.close();

		return graphFactory.openGraph(identifier);
	}
}
