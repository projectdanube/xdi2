package xdi2.messaging.container.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.file.FileJSONGraphFactory;
import xdi2.core.impl.json.file.FileJSONStore;

public class FileJSONGraphMessagingContainerTest extends AbstractGraphMessagingContainerTest {

	private static FileJSONGraphFactory graphFactory = new FileJSONGraphFactory();

	@Override
	protected Graph openGraph(String identifier) throws IOException {

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
