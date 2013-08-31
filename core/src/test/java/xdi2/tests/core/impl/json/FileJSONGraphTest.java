package xdi2.tests.core.impl.json;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.file.FileJSONGraphFactory;
import xdi2.core.impl.json.file.FileJSONStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class FileJSONGraphTest extends AbstractGraphTest {

	private static FileJSONGraphFactory graphFactory = new FileJSONGraphFactory();

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		FileJSONStore.cleanup();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		FileJSONStore.cleanup();
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
