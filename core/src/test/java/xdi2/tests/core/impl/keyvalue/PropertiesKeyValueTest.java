package xdi2.tests.core.impl.keyvalue;

import java.io.File;
import java.io.IOException;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.properties.PropertiesKeyValueStore;

public class PropertiesKeyValueTest extends AbstractKeyValueTest {

	@Override
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		File file = new File(".", "xdi2-test-graph." + id + ".properties");
		if (file.exists()) file.delete();

		// open file

		PropertiesKeyValueStore keyValueStore;

		keyValueStore = new PropertiesKeyValueStore(file, true);
		keyValueStore.load();

		// done

		return keyValueStore;
	}
}
