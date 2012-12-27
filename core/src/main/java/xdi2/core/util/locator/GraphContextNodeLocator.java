package xdi2.core.util.locator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.xri3.impl.XDI3Segment;

public class GraphContextNodeLocator implements ContextNodeLocator {

	private Graph graph;

	public GraphContextNodeLocator(Graph graph) {

		this.graph = graph;
	}

	@Override
	public XDI3Segment getContextNodeXri(XDI3Segment xri) {

		return xri;
	}

	@Override
	public ContextNode locateContextNode(XDI3Segment xri) {

		return this.getGraph().findContextNode(xri, false);
	}

	public Graph getGraph() {

		return this.graph;
	}
}
