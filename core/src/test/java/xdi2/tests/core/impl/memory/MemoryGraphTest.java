package xdi2.tests.core.impl.memory;

import xdi2.core.GraphFactory;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class MemoryGraphTest extends AbstractGraphTest {

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	@Override
	protected GraphFactory getGraphFactory() {

		return graphFactory;
	}

	@Override
	protected boolean supportsPersistence() {

		return false;
	}
}
