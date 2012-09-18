package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.util.JSPolicyExpressionUtil;
import xdi2.core.features.multiplicity.XdiCollection;

public abstract class PolicyExpressionComponent implements Serializable,
		Comparable<PolicyExpressionComponent> {
	protected ContextNode contextNode;
	private static final long serialVersionUID = 1604380462449272149L;


	protected PolicyExpressionComponent(ContextNode contextNode) {
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
				|| NotExpression.isValid(c) ) {
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

	public void addLiteralExpression(String expr){
		if(null == expr || expr.isEmpty()){
			return;
		}

		ContextNode c = XdiCollection.fromContextNode(contextNode).createAttributeMember().getContextNode();
		c.createLiteral(expr);
	}
	// TODO : Add remove function. Consider URL decoding before comparing.
//	public void removeLiteralExpression(String str){
//		Iterator<Literal> allLiterals = contextNode.getAllLiterals();
//		Literal toDelete = null;
//		for(;allLiterals.hasNext();){
//			Literal l = allLiterals.next();
//			if(l.getLiteralData().equalsIgnoreCase(str)){
//				toDelete = l;
//				break;
//			}
//		}
//		if(toDelete != null){		
//			toDelete.delete();
//			toDelete.getContextNode().delete();
//			
//		}
//		
//	}

	public  boolean evaluate(Context cx , Scriptable scope){
		return true;
	}
	
	protected boolean evaluateChildBranch(ContextNode childNode , Context cx , Scriptable scope){
		boolean childExprEvalResult = false;
		
		if (AndExpression.isValid(childNode)) {
			AndExpression andChild = AndExpression
					.fromContextNode(childNode);

			childExprEvalResult = andChild.evaluate(cx,scope);

		} else if (OrExpression.isValid(childNode)) {
			OrExpression orChild = OrExpression.fromContextNode(childNode);

			childExprEvalResult = orChild.evaluate(cx,scope);
		} else if (NotExpression.isValid(childNode)) {
			NotExpression notChild = NotExpression
					.fromContextNode(childNode);

			childExprEvalResult = notChild.evaluate(cx,scope);
		} else if (childNode.getLiteral() != null) {

			
			String literalValue = childNode.getLiteral().getLiteralData();

			childExprEvalResult = JSPolicyExpressionUtil
					.evaluateJSExpression(literalValue, cx,scope);

		}
		return childExprEvalResult;
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
