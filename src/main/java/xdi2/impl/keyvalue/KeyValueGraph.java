package xdi2.impl.keyvalue;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.impl.AbstractGraph;

public class KeyValueGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -1056367553713824301L;

	private final KeyValueStore keyValueStore;

	private final boolean supportGetContextNodes;
	private final boolean supportGetRelations;

	private final KeyValueContextNode rootContextNode;

	KeyValueGraph(KeyValueStore keyValueStore, boolean supportGetContextNodes, boolean supportGetRelations) {

		this.keyValueStore = keyValueStore;

		this.supportGetContextNodes = supportGetContextNodes;
		this.supportGetRelations = supportGetRelations;

		this.rootContextNode = new KeyValueContextNode(this, null, keyValueStore, "()", null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.rootContextNode;
	}

	@Override
	public void close() {

		this.keyValueStore.close();
	}

	/*
	 * Methods related to transactions
	 */

	protected void internalBeginTransaction() {

		this.keyValueStore.beginTransaction();
	}

	protected void internalCommitTransaction() {

		this.keyValueStore.commitTransaction();
	}

	protected void internalRollbackTransaction() {

		this.keyValueStore.rollbackTransaction();
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
}
