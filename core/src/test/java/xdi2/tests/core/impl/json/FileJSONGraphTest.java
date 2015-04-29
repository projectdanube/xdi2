package xdi2.tests.core.impl.json;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.file.FileJSONGraphFactory;
import xdi2.core.impl.json.file.FileJSONStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class FileJSONGraphTest extends AbstractGraphTest {

	private static FileJSONGraphFactory graphFactory = new FileJSONGraphFactory();

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

	@Override
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return true;
	}
}
