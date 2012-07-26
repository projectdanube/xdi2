package xdi2.tests.core.impl.keyvalue;

import java.io.File;
import java.io.IOException;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueStore;

public class PropertiesKeyValueTest extends AbstractKeyValueTest {

	@Override
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		String path = "xdi2-test-graph." + id + ".properties";

		File file = new File(path);
		if (file.exists()) file.delete();
		
		// open file

		PropertiesKeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(path, false);
		keyValueStore.load();

		// done

		return keyValueStore;
	}
}
