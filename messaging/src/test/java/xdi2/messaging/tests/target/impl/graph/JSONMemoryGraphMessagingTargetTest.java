package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.memory.MemoryJSONGraphFactory;

public class JSONMemoryGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MemoryJSONGraphFactory graphFactory = new MemoryJSONGraphFactory();

	static {

	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
