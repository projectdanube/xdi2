package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;

public class BasicRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -8757473050724884998L;

	private XDIAddress XDIaddress;
	private XDIAddress targetContextNodeXDIAddress;

	public BasicRelation(ContextNode contextNode, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		super(contextNode);

		this.XDIaddress = XDIaddress;
		this.targetContextNodeXDIAddress = targetContextNodeXDIAddress;
	}

	public BasicRelation(XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		this(new BasicContextNode(new BasicGraph(null, null), null, null, null, null, null), XDIaddress, targetContextNodeXDIAddress);
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	@Override
	public XDIAddress getTargetContextNodeXDIAddress() {

		return this.targetContextNodeXDIAddress;
	}
}
