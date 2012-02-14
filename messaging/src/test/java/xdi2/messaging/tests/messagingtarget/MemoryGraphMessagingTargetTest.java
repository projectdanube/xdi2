package xdi2.messaging.tests.messagingtarget;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class MemoryGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected Graph openNewGraph(String id) {

		return this.graphFactory.openGraph();
	}
}
