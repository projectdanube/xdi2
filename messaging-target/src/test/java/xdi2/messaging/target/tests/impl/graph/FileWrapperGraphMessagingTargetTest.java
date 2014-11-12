package xdi2.messaging.target.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.wrapped.file.FileWrapperGraphFactory;
import xdi2.core.impl.wrapped.file.FileWrapperStore;

public class FileWrapperGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static FileWrapperGraphFactory graphFactory = new FileWrapperGraphFactory();

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		FileWrapperStore.cleanup();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		FileWrapperStore.cleanup();
	}

	@Override
	protected Graph openGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
