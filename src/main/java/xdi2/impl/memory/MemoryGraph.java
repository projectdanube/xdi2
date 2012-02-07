package xdi2.impl.memory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.impl.AbstractGraph;

public class MemoryGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = 8979035878235290607L;

	int sortmode;

	private MemoryContextNode rootContextNode;

	MemoryGraph(int sortmode) {

		this.sortmode = sortmode;

		this.rootContextNode = new MemoryContextNode(this, null);
		this.rootContextNode.arcXri = null;
	}

	@Override
	public ContextNode getRootContextNode() {

		return rootContextNode;
	}

	@Override
	public void close() {

	}
}
