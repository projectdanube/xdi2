package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class JSONRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = 60162590532295669L;

	private XDIAddress arc;
	private XDIAddress targetContextNodeAddress;

	public JSONRelation(ContextNode contextNode, XDIAddress arc, XDIAddress targetContextNodeAddress) {

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
