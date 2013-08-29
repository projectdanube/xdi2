package xdi2.core.impl.wrapped;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.impl.memory.MemoryContextNode;
import xdi2.core.impl.memory.MemoryGraph;

public class WrappedGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = 8979035878235290607L;

	private WrapperStore wrapperStore;
	private MemoryGraph memoryGraph;

	WrappedGraph(WrappedGraphFactory graphFactory, String identifier, WrapperStore wrapper, MemoryGraph memoryGraph) {

		super(graphFactory, identifier);

		this.wrapperStore = wrapper;
		this.memoryGraph = memoryGraph;

		this.getWrapperStore().load(this.getMemoryGraph());
	}

	@Override
	public ContextNode getRootContextNode() {

		MemoryContextNode memoryContextNode = (MemoryContextNode) this.memoryGraph.getRootContextNode();

		return new WrappedContextNode(this, null, memoryContextNode);
	}

	@Override
	public void close() {

		this.getWrapperStore().save(this.getMemoryGraph());
	}

	@Override
	public boolean supportsTransactions() {

		return false;
	}

	@Override
	public void beginTransaction() {

	}

	@Override
	public void commitTransaction() {

		this.getWrapperStore().save(this.getMemoryGraph());
	}

	@Override
	public void rollbackTransaction() {

	}

	public WrapperStore getWrapperStore() {

		return this.wrapperStore;
	}

	public MemoryGraph getMemoryGraph() {

		return this.memoryGraph;
	}
}
