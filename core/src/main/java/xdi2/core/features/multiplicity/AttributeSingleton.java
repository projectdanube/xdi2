package xdi2.core.features.multiplicity;

import java.io.Serializable;

import xdi2.core.ContextNode;

/**
 * An XDI attribute singleton according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class AttributeSingleton implements Serializable, Comparable<AttributeSingleton> {

	private static final long serialVersionUID = -5769813522592588864L;

	private ContextNode contextNode;

	protected AttributeSingleton(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isAttributeSingletonArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute singleton.
	 * @return The XDI attribute singleton.
	 */
	public static AttributeSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new AttributeSingleton(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI attribute singleton is bound.
	 * @return A context node that represents the XDI attribute singleton.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
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

		if (object == null || ! (object instanceof AttributeSingleton)) return false;
		if (object == this) return true;

		AttributeSingleton other = (AttributeSingleton) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(AttributeSingleton other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
