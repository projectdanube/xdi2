package xdi2.messaging.container.tests.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;

public class MapGraphMessagingContainerTest extends AbstractGraphMessagingContainerTest {

	private static MapKeyValueGraphFactory graphFactory = new MapKeyValueGraphFactory();

	@Override
	protected Graph openGraph(String id) throws IOException {

		return graphFactory.openGraph();
	}
}
