package xdi2.tests.core.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class MemoryGraphTest extends AbstractGraphTest {

	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openNewGraph(String identifier) {

		return this.graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		return graph;
	}
}
