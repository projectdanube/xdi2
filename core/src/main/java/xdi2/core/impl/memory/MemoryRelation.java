package xdi2.core.impl.memory;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XDIAddress arc;
	private XDIAddress targetContextNodeAddress;

	MemoryRelation(MemoryContextNode contextNode, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		super(contextNode);

		this.arc = arc;
		this.targetContextNodeAddress = targetContextNodeAddress;
	}

	@Override
	public XDIAddress getArc() {

		return this.arc;
	}

	@Override
	public XDIAddress getTargetContextNodeAddress() {

		return this.targetContextNodeAddress;
	}
}
