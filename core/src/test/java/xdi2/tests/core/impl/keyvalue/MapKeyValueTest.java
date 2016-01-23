package xdi2.tests.core.impl.keyvalue;

import java.io.IOException;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.map.DefaultMapFactory;
import xdi2.core.impl.keyvalue.map.DefaultSetFactory;
import xdi2.core.impl.keyvalue.map.MapKeyValueStore;

public class MapKeyValueTest extends AbstractKeyValueTest {

	@Override
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		// open store

		KeyValueStore keyValueStore = new MapKeyValueStore(new DefaultMapFactory().newMap(), new DefaultSetFactory());
		keyValueStore.init();

		// done

		return keyValueStore;
	
	}
}
