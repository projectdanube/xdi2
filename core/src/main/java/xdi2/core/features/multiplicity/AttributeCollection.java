package xdi2.core.features.multiplicity;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;

/**
 * An XDI attribute collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class AttributeCollection implements Serializable, Comparable<AttributeCollection> {

	private static final long serialVersionUID = -76507804965389823L;

	private ContextNode contextNode;

	protected AttributeCollection(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isAttributeCollectionArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute collection bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute collection.
	 * @return The XDI attribute collection.
	 */
	public static AttributeCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new AttributeCollection(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI attribute collection is bound.
	 * @return A context node that represents the XDI attribute collection.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/**
	 * Creates a new member and adds it to this XDI attribute collection.
	 * @return The newly created member.
	 */
	public ContextNode createMember() {
		
		return this.getContextNode().createContextNode(Multiplicity.attributeMemberArcXri());
	}

	/**
	 * Returns all members in this XDI attribute collection.
	 * @return An iterator over all members.
	 */
	public ReadOnlyIterator<ContextNode> getMembers() {

		// look for valid relations

		Iterator<ContextNode> members = this.getContextNode().getContextNodes();

		return new SelectingIterator<ContextNode> (members) {

			@Override
			public boolean select(ContextNode member) {

				return Multiplicity.isAttributeMemberArcXri(member.getArcXri());
			}
		};
	}

	/**
	 * Returns the number of members in this XDI attribute collection.
	 */
	public int getMemberCount() {

		ReadOnlyIterator<ContextNode> iterator = this.getMembers();

		return new IteratorCounter(iterator).count();
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

		if (object == null || ! (object instanceof AttributeCollection)) return false;
		if (object == this) return true;

		AttributeCollection other = (AttributeCollection) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(AttributeCollection other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
