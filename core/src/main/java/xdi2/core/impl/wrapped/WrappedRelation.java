package xdi2.core.impl.wrapped;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.impl.memory.MemoryRelation;
import xdi2.core.syntax.XDIAddress;

public class WrappedRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -3710590809580009142L;

	private MemoryRelation memoryRelation;

	WrappedRelation(WrappedContextNode contextNode, MemoryRelation memoryRelation) {

		super(contextNode);

		this.memoryRelation = memoryRelation;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.memoryRelation.getXDIAddress();
	}

	@Override
	public XDIAddress getTargetXDIAddress() {

		return this.memoryRelation.getTargetXDIAddress();
	}
}
