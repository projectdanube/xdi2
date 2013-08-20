package xdi2.tests.core.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapGraphFactory;

public class MapGraphTest extends AbstractGraphTest {

	private MapGraphFactory graphFactory = new MapGraphFactory();

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return this.graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		return graph;
	}
}
