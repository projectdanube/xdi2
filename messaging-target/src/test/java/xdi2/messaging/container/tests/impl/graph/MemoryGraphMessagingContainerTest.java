package xdi2.messaging.container.tests.impl.graph;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class MemoryGraphMessagingContainerTest extends AbstractGraphMessagingContainerTest {

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openGraph(String id) {

		return graphFactory.openGraph();
	}
}
