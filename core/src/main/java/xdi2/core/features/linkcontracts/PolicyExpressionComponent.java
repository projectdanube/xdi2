package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.features.linkcontracts.util.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.util.XDIPolicyExpression;

public abstract class PolicyExpressionComponent implements Serializable,
		Comparable<PolicyExpressionComponent> {
	protected ContextNode contextNode;
	private static final long serialVersionUID = 1604380462449272149L;

	protected PolicyExpressionComponent(ContextNode contextNode) {

		if (contextNode == null)
			throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/**
	 * Checks if a context node is a valid PolicyExpression.
	 * 
	 * @param contextNode
	 *            The context node to check.
	 * @return True if the context node is a valid XDI link contract.
	 */
	public static boolean isValid(ContextNode c) {

		if (AndExpression.isValid(c) || OrExpression.isValid(c)
				|| NotExpression.isValid(c) || LiteralExpression.isValid(c)) {
			return true;

		} else {
			return false;
		}
	}

	protected ContextNode getContextNode() {

		return contextNode;
	}

	/*
	 * Object methods
	 */

	public AndExpression getAndNode(boolean create) {
		AndExpression andNode = null;
		ContextNode nodeExists = this.getContextNode().getContextNode(
				XDILinkContractConstants.XRI_SS_AND);
		if (null != nodeExists) {
			andNode = AndExpression.fromContextNode(nodeExists);
			return andNode;
		}

		if (!this.getContextNode().containsContextNode(
				XDILinkContractConstants.XRI_SS_AND)) {
			andNode = AndExpression.fromContextNode(this.getContextNode()
					.createContextNode(XDILinkContractConstants.XRI_SS_AND));

		}

		return andNode;
	}

	public OrExpression getOrNode(boolean create) {

		OrExpression orNode = null;
		ContextNode nodeExists = this.getContextNode().getContextNode(
				XDILinkContractConstants.XRI_SS_OR);
		if (null != nodeExists) {
			orNode = OrExpression.fromContextNode(nodeExists);
			return orNode;
		}

		if (!this.getContextNode().containsContextNode(
				XDILinkContractConstants.XRI_SS_OR)) {
			orNode = OrExpression.fromContextNode(this.getContextNode()
					.createContextNode(XDILinkContractConstants.XRI_SS_OR));

		}

		return orNode;
	}

	public NotExpression getNotNode(boolean create) {

		NotExpression notNode = null;
		ContextNode nodeExists = this.getContextNode().getContextNode(
				XDILinkContractConstants.XRI_SS_NOT);
		if (null != nodeExists) {
			notNode = NotExpression.fromContextNode(nodeExists);
			return notNode;
		}

		if (!this.getContextNode().containsContextNode(
				XDILinkContractConstants.XRI_SS_NOT)) {
			notNode = NotExpression.fromContextNode(this.getContextNode()
					.createContextNode(XDILinkContractConstants.XRI_SS_NOT));

		}

		return notNode;
	}

	public LiteralExpression getLiteralNode(boolean create) {

		LiteralExpression literalNode = null;
		ContextNode nodeExists = this.getContextNode().getContextNode(
				XDILinkContractConstants.XRI_SS_LITERAL_EXP);
		if (null != nodeExists) {
			literalNode = LiteralExpression.fromContextNode(nodeExists);
			return literalNode;
		}

		if (!this.getContextNode().containsContextNode(
				XDILinkContractConstants.XRI_SS_LITERAL_EXP)) {
			literalNode = LiteralExpression.fromContextNode(this
					.getContextNode().createContextNode(
							XDILinkContractConstants.XRI_SS_LITERAL_EXP));

		}

		return literalNode;
	}
	
	public  String getLogicExpression(){
		return "()";
	}

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if ((object == null) || !(object instanceof PolicyExpressionComponent))
			return false;
		if (object == this)
			return true;

		PolicyExpressionComponent other = (PolicyExpressionComponent) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(PolicyExpressionComponent other) {

		if (other == this || other == null)
			return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}

}
