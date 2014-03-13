package xdi2.tests.core.impl.keyvalue;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class BDBKeyValueGraphTest extends AbstractGraphTest {

	private static BDBKeyValueGraphFactory graphFactory = new BDBKeyValueGraphFactory();

	public static final String DATABASE_PATH = "./xdi2-bdb/";

	static {
		
		graphFactory.setDatabasePath(DATABASE_PATH);
	}

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		BDBKeyValueStore.cleanup(DATABASE_PATH);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		BDBKeyValueStore.cleanup(DATABASE_PATH);
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
