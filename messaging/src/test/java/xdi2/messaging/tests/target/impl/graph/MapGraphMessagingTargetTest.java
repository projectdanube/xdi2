package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapGraphFactory;

public class MapGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MapGraphFactory graphFactory = new MapGraphFactory();

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		return graphFactory.openGraph();
	}
}
