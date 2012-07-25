package xdi2.core.features.multiplicity;

import java.io.Serializable;

import xdi2.core.ContextNode;

public abstract class AbstractMultiplicityContextNode implements Serializable, Comparable<AbstractMultiplicityContextNode> {

	private static final long serialVersionUID = -8756059289169602694L;

	private ContextNode contextNode;

	protected AbstractMultiplicityContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

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

		if (object == null || ! (object instanceof AbstractMultiplicityContextNode)) return false;
		if (object == this) return true;

		AbstractMultiplicityContextNode other = (AbstractMultiplicityContextNode) object;

		// two multiplicity objects are equal if their context nodes are equal

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(AbstractMultiplicityContextNode other) {

		if (other == null || other == this) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
