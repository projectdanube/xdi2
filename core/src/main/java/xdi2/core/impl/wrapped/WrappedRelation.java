package xdi2.core.impl.wrapped;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.impl.memory.MemoryRelation;
import xdi2.core.xri3.XDI3Segment;

public class WrappedRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -3710590809580009142L;

	private MemoryRelation memoryRelation;

	WrappedRelation(WrappedContextNode contextNode, MemoryRelation memoryRelation) {

		super(contextNode);

		this.memoryRelation = memoryRelation;
	}

	@Override
	public XDI3Segment getArcXri() {
		
		return this.memoryRelation.getArcXri();
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {
		
		return this.memoryRelation.getTargetContextNodeXri();
	}
}
