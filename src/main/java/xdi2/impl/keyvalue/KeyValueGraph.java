package xdi2.impl.keyvalue;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.impl.AbstractGraph;

public class KeyValueGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -1056367553713824301L;

	private final boolean supportGetContextNodes;
	private final boolean supportGetRelations;
	private final boolean supportGetLiterals;

	private final KeyValueContextNode rootContextNode;

	KeyValueGraph(KeyValueStore keyValueStore, boolean supportGetContextNodes, boolean supportGetRelations, boolean supportGetLiterals) {

		this.supportGetContextNodes = supportGetContextNodes;
		this.supportGetRelations = supportGetRelations;
		this.supportGetLiterals = supportGetLiterals;

		this.rootContextNode = new KeyValueContextNode(this, null, keyValueStore, "()", null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.rootContextNode;
	}

	@Override
	public void close() {

	}

	/*
	 * Misc methods
	 */

	boolean isSupportGetContextNodes() {

		return this.supportGetContextNodes;
	}

	boolean isSupportGetRelations() {

		return this.supportGetRelations;
	}

	boolean isSupportGetLiterals() {

		return this.supportGetLiterals;
	}
}
