package xdi2.tests.core.impl.json;

import xdi2.core.GraphFactory;
import xdi2.core.impl.json.memory.MemoryJSONGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class MemoryJSONGraphTest extends AbstractGraphTest {

	private static MemoryJSONGraphFactory graphFactory = new MemoryJSONGraphFactory();

	@Override
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return false;
	}
}
