package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;

public class DummyRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -8757473050724884998L;

	private XDIAddress XDIaddress;
	private XDIAddress targetXDIAddress;

	public DummyRelation(ContextNode contextNode, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		super(contextNode);

		this.XDIaddress = XDIaddress;
		this.targetXDIAddress = targetXDIAddress;
	}

	public DummyRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		this(new DummyContextNode(new DummyGraph(null, null), null, null, null, null, null), XDIaddress, targetXDIAddress);
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
