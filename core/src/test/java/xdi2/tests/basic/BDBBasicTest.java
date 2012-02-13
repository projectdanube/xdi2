package xdi2.tests.basic;

import java.io.File;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.bdb.BDBGraphFactory;

public class BDBBasicTest extends BasicTest {

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
	protected Graph openNewGraph(String id) throws IOException {

		String databaseName = "xdi2-graph." + id + ".properties";

		graphFactory.setDatabaseName(databaseName);

		Graph graph = graphFactory.openGraph();
		graph.beginTransaction();

		return graph;
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		graph.close();

		String databaseName = "xdi2-graph." + id + ".properties";

		graphFactory.setDatabaseName(databaseName);

		Graph newGraph = graphFactory.openGraph();
		newGraph.beginTransaction();

		return newGraph;
	}
}
