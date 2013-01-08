package xdi2.webtools.grapher;

import xdi2.core.Statement;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;

public class Drawer2 extends AbstractJUNGDrawer {

	@Override
	public Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph) {

		return new FRLayout2<Object, Statement> (directedGraph);
	}
}
