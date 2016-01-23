package xdi2.messaging.target.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;

public class MapGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static MapKeyValueGraphFactory graphFactory = new MapKeyValueGraphFactory();

	@Override
	protected Graph openGraph(String id) throws IOException {

		return graphFactory.openGraph();
	}
}
