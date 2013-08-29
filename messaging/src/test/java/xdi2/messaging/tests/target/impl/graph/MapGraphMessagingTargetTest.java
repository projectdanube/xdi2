package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;

public class MapGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MapKeyValueGraphFactory graphFactory = new MapKeyValueGraphFactory();

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		return graphFactory.openGraph();
	}
}
