package xdi2.webtools.grapher;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import xdi2.core.Statement;

public class Drawer1 extends AbstractJUNGDrawer {

	@Override
	public Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph) {

		KKLayout<Object, Statement> layout = new KKLayout<Object, Statement> (directedGraph);

		return layout;
	}
}
