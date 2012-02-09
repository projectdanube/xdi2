package xdi2.tests.basic;

import java.io.File;
import java.io.IOException;

import xdi2.Graph;
import xdi2.impl.keyvalue.bdb.BDBGraphFactory;

public class BDBBasicTest extends BasicTest {

	private static BDBGraphFactory graphFactory = new BDBGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		for (File file : new File(graphFactory.getDatabasePath()).listFiles()) {

			file.delete();
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

		graph = graphFactory.openGraph();
		graph.beginTransaction();
		
		return graph;
	}
}
