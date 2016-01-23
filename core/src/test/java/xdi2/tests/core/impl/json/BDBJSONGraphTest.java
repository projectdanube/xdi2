package xdi2.tests.core.impl.json;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.bdb.BDBJSONGraphFactory;
import xdi2.core.impl.json.bdb.BDBJSONStore;
import xdi2.tests.core.impl.AbstractGraphTest;

public class BDBJSONGraphTest extends AbstractGraphTest {

	private static BDBJSONGraphFactory graphFactory = new BDBJSONGraphFactory();

	public static final String DATABASE_PATH = "./xdi2-bdb-json/";

	static {
		
		graphFactory.setDatabasePath(DATABASE_PATH);
	}

	@Override
	protected void setUp() throws Exception {

		super.setUp();

		BDBJSONStore.cleanup(DATABASE_PATH);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		BDBJSONStore.cleanup(DATABASE_PATH);
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
