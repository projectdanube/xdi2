package xdi2.core.features.multiplicity;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.SelectingMappingIterator;

/**
 * An XDI attribute collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class AttributeCollection extends AbstractCollection<AttributeSingleton> implements Serializable, Comparable<AttributeCollection> {

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
	 * Creates a new attribute singleton and adds it to this XDI attribute collection.
	 * @return The newly created attribute singleton.
	 */
	public AttributeSingleton createAttributeSingleton() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.attributeCollectionMemberArcXri());
		
		return AttributeSingleton.fromContextNode(contextNode);
	}

	/**
	 * Returns all attribute singletons in this XDI attribute collection.
	 * @return An iterator over all attribute singletons.
	 */
	@Override
	public Iterator<AttributeSingleton> iterator() {

		// look for context nodes that are valid attribute singletons

		Iterator<ContextNode> contextNodes = this.getContextNode().getContextNodes();

		return new SelectingMappingIterator<ContextNode, AttributeSingleton> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return Multiplicity.isAttributeCollectionMemberArcXri(contextNode.getArcXri());
			}

			@Override
			public AttributeSingleton map(ContextNode contextNode) {

				return AttributeSingleton.fromContextNode(contextNode);
			}
		};
	}

	/**
	 * Returns the number of attribute singletons in this XDI attribute collection.
	 */
	@Override
	public int size() {

		return new IteratorCounter(this.iterator()).count();
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

	@Override
	public int compareTo(AttributeCollection other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
