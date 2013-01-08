package xdi2.webtools.grapher;

import xdi2.core.Statement;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;

public class Drawer3 extends AbstractJUNGDrawer {

	@Override
	public Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph) {

		return new ISOMLayout<Object, Statement> (directedGraph);
	}
}
