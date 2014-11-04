package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;

public abstract class AbstractNode implements Node {

	private static final long serialVersionUID = 151088659284780089L;

	private ContextNode contextNode;

	private XDIAddress XDIaddress;

	public AbstractNode(ContextNode contextNode) {

		this.contextNode = contextNode;

		this.XDIaddress = null;
	}

	@Override
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	@Override
	public ContextNode getContextNode(int arcs) {

		if (arcs < 0) throw new IllegalArgumentException("Invalid number of arcs: " + arcs);
		if (arcs == 0 && this instanceof ContextNode) return (ContextNode) this;
		if (arcs == 0 && this instanceof LiteralNode) throw new IllegalArgumentException("Invalid number of arcs for literal node: " + arcs);

		ContextNode contextNode = this.getContextNode();

		for (int i=0; i<arcs-1; i++) {

			contextNode = contextNode.getContextNode();
			if (contextNode == null) break;
		}

		return contextNode;
	}

	@Override
	public XDIAddress getXDIAddress() {

		if (this.XDIaddress == null) {

			ContextNode contextNode = this.getContextNode();
			
			if (contextNode == null) {

				this.XDIaddress = XDIConstants.XDI_ADD_ROOT;
			} else {

				this.XDIaddress = XDIAddressUtil.concatXDIAddresses(contextNode.getXDIAddress(), this.getXDIArc());
			}
		}

		return this.XDIaddress;
	}
}
