package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.AbstractJSONGraphFactory;
import xdi2.core.impl.json.memory.MemoryJSONStore;

public class JSONMemoryGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static AbstractJSONGraphFactory graphFactory = new AbstractJSONGraphFactory();

	static {

		graphFactory.setJsonStore(new MemoryJSONStore());
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
