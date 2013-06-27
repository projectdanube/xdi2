package xdi2.messaging.tests.target.impl.graph;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class MemoryGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openNewGraph(String id) {

		return graphFactory.openGraph();
	}
}
