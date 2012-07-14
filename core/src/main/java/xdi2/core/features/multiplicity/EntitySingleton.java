package xdi2.core.features.multiplicity;

import java.io.Serializable;

import xdi2.core.ContextNode;

/**
 * An XDI entity singleton according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class EntitySingleton implements Serializable, Comparable<EntitySingleton> {

	private static final long serialVersionUID = -1075885367630005576L;

	private ContextNode contextNode;

	protected EntitySingleton(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isEntitySingletonArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static EntitySingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new EntitySingleton(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI entity singleton is bound.
	 * @return A context node that represents the XDI entity singleton.
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

		if (object == null || ! (object instanceof EntitySingleton)) return false;
		if (object == this) return true;

		EntitySingleton other = (EntitySingleton) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(EntitySingleton other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
