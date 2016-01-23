package xdi2.messaging.target.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueStore;

public class PropertiesKeyValueGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static PropertiesKeyValueGraphFactory graphFactory = new PropertiesKeyValueGraphFactory();

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		PropertiesKeyValueStore.cleanup();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		PropertiesKeyValueStore.cleanup();
	}

	@Override
	protected Graph openGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
