package xdi2.tests.core.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.memory.MemoryJSONGraphFactory;

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
