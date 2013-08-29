package xdi2.tests.core.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;

public class MapGraphTest extends AbstractGraphTest {

	private MapKeyValueGraphFactory graphFactory = new MapKeyValueGraphFactory();

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return this.graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		return graph;
	}
}
