package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;

public class BasicRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -8757473050724884998L;

	private XDIAddress arc;
	private XDIAddress targetContextNodeAddress;

	public BasicRelation(ContextNode contextNode, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		super(contextNode);

		this.arc = arc;
		this.targetContextNodeAddress = targetContextNodeAddress;
	}

	public BasicRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		this(new BasicContextNode(new BasicGraph(null, null), null, null, null, null, null), arc, targetContextNodeAddress);
	}

	@Override
	public XDIAddress getAddress() {

		return this.arc;
	}

	@Override
	public XDIAddress getTargetContextNodeAddress() {

		return this.targetContextNodeAddress;
	}
}
