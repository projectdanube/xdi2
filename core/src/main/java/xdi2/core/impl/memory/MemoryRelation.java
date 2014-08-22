package xdi2.core.impl.memory;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XDIAddress address;
	private XDIAddress targetContextNodeAddress;

	MemoryRelation(MemoryContextNode contextNode, XDIAddress address, XDIAddress targetContextNodeAddress) {

		super(contextNode);

		this.address = address;
		this.targetContextNodeAddress = targetContextNodeAddress;
	}

	@Override
	public XDIAddress getAddress() {

		return this.address;
	}

	@Override
	public XDIAddress getTargetContextNodeAddress() {

		return this.targetContextNodeAddress;
	}
}
