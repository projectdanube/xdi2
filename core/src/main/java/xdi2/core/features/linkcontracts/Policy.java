package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;

/**
 * An XDI policy belonging to an XDI link contract, represented as a context node.
 * 
 * @author markus
 */
public final class Policy implements Serializable, Comparable<Policy> {

	private static final long serialVersionUID = -9212794041490417047L;

	private LinkContract linkContract;
	private ContextNode contextNode;

	protected Policy(LinkContract linkContract, ContextNode contextNode) {

		if (linkContract == null || contextNode == null) throw new NullPointerException();

		this.linkContract = linkContract;
		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XDILinkContractConstants.XRI_SS_IF.equals(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI policy bound to a given context node.
	 * @param linkContract The XDI link contract to which this XDI policy belongs.
	 * @param contextNode The context node that is an XDI policy.
	 * @return The XDI policy.
	 */
	public static Policy fromLinkContractAndContextNode(LinkContract linkContract, ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new Policy(linkContract, contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the link contract to which this policy belongs.
	 * @return A message envelope.
	 */
	public LinkContract getLinkContract() {

		return this.linkContract;
	}

	/**
	 * Returns the underlying context node to which this policy is bound.
	 * @return A context node that represents the policy.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	// ...
	// here go methods for working with the policy ...
	// ...

	
	
	
	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Policy)) return false;
		if (object == this) return true;

		Policy other = (Policy) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(Policy other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
