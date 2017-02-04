package xdi2.messaging.container.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.bdb.BDBKeyValueStore;

public class BDBKeyValueGraphMessagingContainerTest extends AbstractGraphMessagingContainerTest {

	private static BDBKeyValueGraphFactory graphFactory = new BDBKeyValueGraphFactory();

	public static final String DATABASE_PATH = "./xdi2-bdb-keyvalue/";

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
	protected Graph openGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
