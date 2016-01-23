package xdi2.tests.core.impl.keyvalue;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class PropertiesKeyValueGraphTest extends AbstractGraphTest {

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
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return true;
	}
}
