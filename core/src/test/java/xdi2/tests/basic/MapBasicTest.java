package xdi2.tests.basic;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.map.MapGraphFactory;

public class MapBasicTest extends BasicTest {

	private MapGraphFactory graphFactory = new MapGraphFactory();

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		return this.graphFactory.openGraph();
	}

	@Override
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		return graph;
	}
}
