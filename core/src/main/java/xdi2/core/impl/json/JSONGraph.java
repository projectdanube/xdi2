package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraph;

public class JSONGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -7459785412219244590L;

	private final JSONStore jsonStore;

	private final JSONContextNode jsonRootContextNode;

	JSONGraph(GraphFactory graphFactory, String identifier, JSONStore jsonStore) {

		super(graphFactory, identifier);

		this.jsonStore = jsonStore;

		this.jsonRootContextNode = new JSONContextNode(this, null, jsonStore, "()", null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.jsonRootContextNode;
	}

	@Override
	public void close() {

		System.out.println(((AbstractJSONStore) this.jsonStore).getLogBuffer());
		
		this.jsonStore.close();
	}

	/*
	 * Misc methods
	 */

	/**
	 * Returns the JSON store this graph is based on.
	 * WARNING: Do not alter the contents of the store using this method, or your XDI graph may get corrupted.
	 * @return The JSON store backing this graph.
	 */
	public JSONStore getJSONStore() {

		return this.jsonStore;
	}
}
