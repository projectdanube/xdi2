package xdi2.tests;

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;

public class MemoryBasicTest extends BasicTest {

	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openNewGraph(String id) {

		return this.graphFactory.openGraph();
	}
}
