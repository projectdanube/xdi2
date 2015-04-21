package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class JSONRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = 60162590532295669L;

	private XDIAddress XDIaddress;
	private XDIAddress targetXDIAddress;

	public JSONRelation(ContextNode contextNode, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

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
