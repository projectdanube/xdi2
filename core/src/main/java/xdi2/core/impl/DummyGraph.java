package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;

public class DummyGraph extends AbstractGraph implements Graph {

	protected DummyGraph(GraphFactory graphFactory, String identifier) {

		super(graphFactory, identifier);
	}

	private static final long serialVersionUID = 4690232105168518399L;

	@Override
	public ContextNode getRootContextNode(boolean subgraph) {

		return new DummyContextNode(this, null, null, null, null, null);
	}

	@Override
	public void close() {

		throw new UnsupportedOperationException("Not supported.");
	}
}
