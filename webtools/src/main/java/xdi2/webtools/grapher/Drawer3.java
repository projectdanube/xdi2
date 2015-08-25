package xdi2.webtools.grapher;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import xdi2.core.Statement;

public class Drawer3 extends AbstractJUNGDrawer {

	@Override
	public Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph) {

		return new ISOMLayout<Object, Statement> (directedGraph);
	}
}
