package xdi2.tests.core.impl.json;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.memory.MemoryJSONGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class MemoryJSONGraphTest extends AbstractGraphTest {

	private static MemoryJSONGraphFactory graphFactory = new MemoryJSONGraphFactory();

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

 		return graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		return graph;
	}
}
