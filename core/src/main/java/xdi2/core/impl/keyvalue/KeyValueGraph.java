package xdi2.core.impl.keyvalue;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.AbstractGraph;

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

	@Override
	public boolean supportsTransactions() {

		return this.keyValueStore.supportsTransactions();
	}

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

	/**
	 * Returns the key/value store this graph is based on.
	 * WARNING: Do not alter the contents of the store using this method, or your XDI graph may get corrupted.
	 * @return The key/value store backing this graph.
	 */
	public KeyValueStore getKeyValueStore() {

		return this.keyValueStore;
	}

	/**
	 * @return True, if this key/value graph supports enumerating contexts.
	 */
	public boolean isSupportGetContextNodes() {

		return this.supportGetContextNodes;
	}

	/**
	 * @return True, if this key/value graph supports enumerating relations.
	 */
	public boolean isSupportGetRelations() {

		return this.supportGetRelations;
	}
}
