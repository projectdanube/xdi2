package xdi2.tests.core.impl.wrapped;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.wrapped.file.FileWrapperGraphFactory;
import xdi2.core.impl.wrapped.file.FileWrapperStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class FileWrapperGraphTest extends AbstractGraphTest {

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
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		graph.close();

		return graphFactory.openGraph(identifier);
	}
}
