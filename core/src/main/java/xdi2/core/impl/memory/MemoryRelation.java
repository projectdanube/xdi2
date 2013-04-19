package xdi2.core.impl.memory;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.XDI3Segment;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XDI3Segment arcXri;
	private XDI3Segment targetContextNodeXri;

	MemoryRelation(MemoryContextNode contextNode, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		super(contextNode);

		this.arcXri = arcXri;
		this.targetContextNodeXri = targetContextNodeXri;
	}

	@Override
	public XDI3Segment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {

		return this.targetContextNodeXri;
	}
}
