package xdi2.messaging.tests.messagingtarget;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapGraphFactory;

public class MapGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private MapGraphFactory graphFactory = new MapGraphFactory();

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		return this.graphFactory.openGraph();
	}
}
