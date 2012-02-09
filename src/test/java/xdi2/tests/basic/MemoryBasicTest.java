package xdi2.tests.basic;

import java.io.IOException;

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;

public class MemoryBasicTest extends BasicTest {

	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openNewGraph(String id) {

		return this.graphFactory.openGraph();
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		return graph;
	}
}
