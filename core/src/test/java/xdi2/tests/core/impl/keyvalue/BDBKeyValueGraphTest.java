package xdi2.tests.core.impl.keyvalue;

import java.io.File;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class BDBKeyValueGraphTest extends AbstractGraphTest {

	private static BDBKeyValueGraphFactory graphFactory = new BDBKeyValueGraphFactory();

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

		return graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		graph.close();

		return graphFactory.openGraph(identifier);
	}
}
