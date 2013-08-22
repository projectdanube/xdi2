package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.file.FileJSONGraphFactory;
import xdi2.core.impl.json.file.FileJSONStore;

public class FileJSONGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static FileJSONGraphFactory graphFactory = new FileJSONGraphFactory();

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		FileJSONStore.cleanup();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		FileJSONStore.cleanup();
	}
}
