package xdi2.tests.core.impl.keyvalue;

import java.io.File;
import java.io.IOException;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueStore;

public class PropertiesKeyValueTest extends AbstractKeyValueTest {

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
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		String path = "xdi2-properties-keyvalue-graph." + id + ".properties";

		File file = new File(path);
		if (file.exists()) file.delete();
		
		// open store

		KeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(path);
		keyValueStore.init();

		// done

		return keyValueStore;
	}
}
