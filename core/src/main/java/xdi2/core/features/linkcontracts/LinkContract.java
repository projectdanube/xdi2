package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.features.linkcontracts.util.XDILinkContractConstants;

/**
 * An XDI link contract, represented as a context node.
 * 
 * @author markus
 */
public final class LinkContract implements Serializable, Comparable<LinkContract> {

	private static final long serialVersionUID = 1604380462449272148L;

	private ContextNode contextNode;

	protected LinkContract(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();
		
		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI link contract.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI link contract.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XDILinkContractConstants.XRI_SS_DO.equals(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given context node.
	 * @param contextNode The context node that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new LinkContract(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI link contract is bound.
	 * @return A context node that represents the XDI link contract.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	// ...
	// here go methods for working with the link contract ...
	// these methods should operate on the link contract's context node
	// ...

	/**
	 * Returns an existing XDI policy in this XDI link contract, or creates a new one.
	 * @param create Whether to create an XDI policy if it does not exist.
	 * @return The existing or newly created XDI policy.
	 */
	public Policy getPolicy(boolean create) {

		ContextNode contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_IF);
		if (contextNode == null && create) contextNode = this.getContextNode().createContextNode(XDILinkContractConstants.XRI_SS_IF); 
		if (contextNode == null) return null;

		return new Policy(this, contextNode);
	}

	public void addAssignee(ContextNode assignee) {
		
	}
	
	public void removeAssignee(ContextNode assignee) {
		
	}

	public void getAssignees() {
		
	}
	
	public void addPermission() {
		
	}
	
	public void removePermission() {
		
	}
	
	public void getPermissions() {
		
	}
	
	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof LinkContract)) return false;
		if (object == this) return true;

		LinkContract other = (LinkContract) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(LinkContract other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
