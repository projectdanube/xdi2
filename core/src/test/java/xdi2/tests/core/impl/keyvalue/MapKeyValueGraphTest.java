package xdi2.tests.core.impl.keyvalue;

import xdi2.core.GraphFactory;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class MapKeyValueGraphTest extends AbstractGraphTest {

	private static MapKeyValueGraphFactory graphFactory = new MapKeyValueGraphFactory();

	@Override
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return false;
	}
}
