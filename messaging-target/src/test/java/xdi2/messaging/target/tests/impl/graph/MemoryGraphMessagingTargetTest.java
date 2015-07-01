package xdi2.messaging.target.tests.impl.graph;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class MemoryGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openGraph(String id) {

		return graphFactory.openGraph();
	}
}
