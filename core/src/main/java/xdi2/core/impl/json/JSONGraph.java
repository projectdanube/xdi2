package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraph;

public class JSONGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -7459785412219244590L;

	private JSONContextNode jsonRootContextNode;

	JSONGraph(GraphFactory graphFactory, String identifier, JSONStore jsonStore) {

		super(graphFactory, identifier);

		String id = identifier != null ? (identifier + "_") : "";

		this.jsonRootContextNode = new JSONContextNode(this, null, null, jsonStore, id);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.jsonRootContextNode;
	}

	@Override
	public void close() {

	}
}
