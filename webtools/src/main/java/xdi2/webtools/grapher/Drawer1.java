package xdi2.webtools.grapher;

import xdi2.core.Statement;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;

public class Drawer1 extends AbstractJUNGDrawer {

	@Override
	public Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph) {

		KKLayout<Object, Statement> layout = new KKLayout<Object, Statement> (directedGraph);

		return layout;
	}
}
