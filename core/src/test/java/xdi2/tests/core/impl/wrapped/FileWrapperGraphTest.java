package xdi2.tests.core.impl.wrapped;

import xdi2.core.GraphFactory;
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
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return false;
	}
}
