package xdi2.core.features.multiplicity;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;

/**
 * An XDI entity collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class EntityCollection implements Serializable, Comparable<EntityCollection> {

	private static final long serialVersionUID = 1455719520426705802L;

	private ContextNode contextNode;

	protected EntityCollection(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isEntityCollectionArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI entity collection bound to a given context node.
	 * @param contextNode The context node that is an XDI entity collection.
	 * @return The XDI entity collection.
	 */
	public static EntityCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new EntityCollection(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI entity collection is bound.
	 * @return A context node that represents the XDI entity collection.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/**
	 * Creates a new member and adds it to this XDI entity collection.
	 * @return The newly created member.
	 */
	public ContextNode createMember() {

		return this.getContextNode().createContextNode(Multiplicity.entityMemberArcXri());
	}

	/**
	 * Returns all members in this XDI entity collection.
	 * @return An iterator over all members.
	 */
	public ReadOnlyIterator<ContextNode> getMembers() {

		// look for valid relations

		Iterator<ContextNode> members = this.getContextNode().getContextNodes();

		return new SelectingIterator<ContextNode> (members) {

			@Override
			public boolean select(ContextNode member) {

				return Multiplicity.isEntityMemberArcXri(member.getArcXri());
			}
		};
	}

	/**
	 * Returns the number of members in this XDI entity collection.
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

		if (object == null || ! (object instanceof EntityCollection)) return false;
		if (object == this) return true;

		EntityCollection other = (EntityCollection) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(EntityCollection other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
