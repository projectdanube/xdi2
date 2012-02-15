package xdi2.tests.core.impl.keyvalue;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.impl.keyvalue.map.DefaultMapFactory;
import xdi2.core.impl.keyvalue.map.DefaultSetFactory;
import xdi2.core.impl.keyvalue.map.MapKeyValueStore;

public class MapKeyValueTest extends AbstractKeyValueTest {

	@Override
	protected KeyValueStore getKeyValueStore(String id) throws IOException {

		Map<String, Set<String>> map = new DefaultMapFactory().newMap();

		return new MapKeyValueStore(map, new DefaultSetFactory());
	}
}
