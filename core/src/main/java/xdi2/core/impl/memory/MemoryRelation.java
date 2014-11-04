package xdi2.core.impl.memory;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XDIAddress XDIaddress;
	private XDIAddress targetXDIAddress;

	MemoryRelation(MemoryContextNode contextNode, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		super(contextNode);

		this.XDIaddress = XDIaddress;
		this.targetXDIAddress = targetXDIAddress;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	@Override
	public XDIAddress getTargetXDIAddress() {

		return this.targetXDIAddress;
	}
}
